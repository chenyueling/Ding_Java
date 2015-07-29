package cn.jpush.alertme.factory.plugins.rss;

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
 * Created by chenyueling on 2015/1/27.
 */
@Path("/rss")
public class RssResource extends BaseResource {

    private static final Logger Log = LoggerFactory.getLogger(RssResource.class);

    public static final String Tag = "Rss";

    public static final String cron = "0 30 8 * * ?";

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {

        String cron = sData.get("cron");

        if (cron == null) {
            cron = RssResource.cron;
        }
        Log.info("[RssResource onServiceCreate] add job to task cron express " + cron);

        try {
            QuartzHelper.addSingleJob(sid, cron, RssJob.class);
        } catch (SchedulerException e) {
            e.printStackTrace();
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
