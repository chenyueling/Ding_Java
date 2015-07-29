package cn.jpush.alertme.factory.plugins.lofter;

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
 * Created by chenyueling on 2014/12/24.
 */
@Path("/lofter")
public class LofterResource extends BaseResource {
    private static Logger Log = LoggerFactory.getLogger(LofterResource.class);
    public static String Tag = "Lofter";
    private static String cron = "0 30 17 *  * ?";
    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        try {
            Log.info("[LofterResource onServiceCreate] create Job cron :" +cron);
            QuartzHelper.addSingleJob(LofterResource.Tag,cron,LofterJob.class);
        } catch (SchedulerException e) {
            Log.info("[LofterResource onServiceCreate] create Job error :" + e.toString());
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
