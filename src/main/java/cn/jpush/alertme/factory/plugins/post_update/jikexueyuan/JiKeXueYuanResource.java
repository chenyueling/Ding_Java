package cn.jpush.alertme.factory.plugins.post_update.jikexueyuan;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import cn.jpush.alertme.factory.util.LogUtil;
import org.quartz.SchedulerException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by ZeFanXie on 15-1-26.
 */
@Path("/post_update/ji_ke_xue_yuan")
public class JiKeXueYuanResource extends BaseResource {
    private static final String cron = "0 */10 * * * ?";

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        // 添加单例定时任务
        try {
            QuartzHelper.addSingleJob(JiKeXueYuanSpider.Tag, cron, JiKeXueYuanSpider.class);
        } catch (SchedulerException e) {
            LogUtil.e(JiKeXueYuanSpider.Tag + " Quartz Init Fail", e);
        }
        LogUtil.d(JiKeXueYuanSpider.Tag + " Timer Running...");
        return super.onServiceCreate(resp, sid, apiSecret, title, sData, cData);
    }
        
    @Override
    protected String getTag() {
        return JiKeXueYuanSpider.Tag;
    }
}
