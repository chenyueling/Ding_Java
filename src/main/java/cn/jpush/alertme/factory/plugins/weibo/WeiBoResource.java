package cn.jpush.alertme.factory.plugins.weibo;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.http.HttpContextHelper;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.Service;
import cn.jpush.alertme.factory.util.StringUtil;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * WeiBo Resource
 * Created by ZeFanXie on 14-12-30.
 */
@Path("/weibo")
public class WeiBoResource extends BaseResource {
    private static final Logger Log = LoggerFactory.getLogger(WeiBoResource.class);
    public static final String Tag = "WEIBO";
    private static final String cron = "0 */1 * * * ?";

    private ServiceDao serviceDao = null;

    @Override
    protected boolean onClientServiceCreate(HttpServletResponse resp, String sid, String cid, Map<String, String> data) {
        // 添加监控UserId
        // 首先从Client Field中获取UserId, 若找不到则在Service Field中查找
        String userId = data.get("user_id");
        if (StringUtil.isEmpty(userId)) {
            if (serviceDao == null) {
                serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
            }
            Service service = serviceDao.findById(sid);
            userId = service.getData().get("user_id");
            if (StringUtil.isEmpty(userId)) {
                HttpContextHelper.writeInvalidParameterResponse(resp, "Invalid Parameter 'user_id'");
                return false;
            }
        }
        WeiBoStore.addCidToUserId(userId, cid);
        WeiBoStore.addFocusUserId(userId);
        return super.onClientServiceCreate(resp, sid, cid, data);
    }

    @Override
    protected boolean onServiceUpdate(HttpServletResponse resp, String sid, String title, Map<String, String> sData, List<CData> cData) {
        // 从Client Field中添加监控UserId
        if (cData.size() > 0) {
            List<String> userIds = new ArrayList<>();
            for (CData data : cData) {
                String userId = data.getData().get("user_id");
                if (!StringUtil.isEmpty(userId)) {
                    userIds.add(userId);
                    WeiBoStore.addCidToUserId(userId, data.cid);
                }
            }
            WeiBoStore.addFocusUserId((String[]) userIds.toArray());
        }
        return super.onServiceUpdate(resp, sid, title, sData, cData);
    }

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        // 添加单例定时任务
        try {
            QuartzHelper.addSingleJob(Tag, cron, SpiderJob.class);
        } catch (SchedulerException e) {
            Log.error(Tag + " Quartz Init Fail:" + e.getMessage());
        }
        Log.debug(Tag + " Timer Running...");


        // 从Client Field中添加监控UserId
        if (cData.size() > 0) {
            for (CData data : cData) {
                String userId = data.getData().get("user_id");
                if (!StringUtil.isEmpty(userId)) {
                    WeiBoStore.addCidToUserId(userId, data.cid);
                    WeiBoStore.addFocusUserId(userId);
                }
            }

        }


        return super.onServiceCreate(resp, sid, apiSecret, title, sData, cData);
    }

    @Override
    protected Logger getLog() {
        return Log;
    }

    @Override
    protected String getTag() {
        return Tag;
    }
}
