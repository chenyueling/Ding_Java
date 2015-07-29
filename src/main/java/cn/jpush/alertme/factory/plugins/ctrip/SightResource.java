package cn.jpush.alertme.factory.plugins.ctrip;

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
 * Created by chenyueling on 2015/1/12.
 */
@Path("/sight")
public class SightResource extends BaseResource {
    private static final Logger Log = LoggerFactory.getLogger(SightResource.class);
    public static final String Tag = "Sight";
    private static final String cron = "0 50 16 ? * 5";
    //private static final String cron = "0 0/2 * * * ?";
    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        Log.info("[SightResource onServiceCreate] add task cron express " + cron);
        try {
            QuartzHelper.addSingleJob(SightResource.Tag, SightResource.cron, SightJob.class);
        } catch (SchedulerException e) {
            Log.info("[SightResource onServiceCreate] add task error " + e.toString());
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
