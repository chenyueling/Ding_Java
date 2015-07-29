package cn.jpush.alertme.factory.plugins.hotnew;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/22.
 */
@Path("/news")
public class NetsNewResource extends BaseResource {
    private static final Logger Log = LoggerFactory.getLogger(NetsNewResource.class);
    public static final String Tag = "NetsNew";

    private static final String  cron = "0 30 08 * * ?" ;


    @Override
    protected boolean onClientServiceCreate(HttpServletResponse resp, String sid, String cid, Map<String, String> data) {
        Log.info("[NetsNewResource onServiceCreate] create Job cron :" +cron);
        try {
            QuartzHelper.addSingleJob(NetsNewResource.Tag,cron,NetsNewJob.class);
        } catch (SchedulerException e) {
            e.printStackTrace();
            Log.info("[NetsNewResource onServiceCreate] create Job error :" + e.toString());
        }
        return super.onClientServiceCreate(resp, sid, cid, data);
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
