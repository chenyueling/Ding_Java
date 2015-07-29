package cn.jpush.alertme.factory.plugins.next;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

/**
 * Next Resource
 * Created by ZeFanXie on 14-12-24.
 */
@Path("/next")
public class NextResource extends BaseResource {
    private static final Logger Log = LoggerFactory.getLogger(NextResource.class);
    public static final String Tag = "NEXT";
    // 每天下午三点半
    private static final String cron = "0 33 15 * * ?";

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        // 添加单例定时任务
        try {
            QuartzHelper.addSingleJob(Tag, cron, SpiderJob.class);
        } catch (SchedulerException e) {
            Log.error(Tag + " Quartz Init Fail:" + e.getMessage());
        }
        Log.debug(Tag + " Timer Running...");
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
