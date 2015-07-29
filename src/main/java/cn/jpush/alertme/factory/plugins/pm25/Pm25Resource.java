package cn.jpush.alertme.factory.plugins.pm25;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import org.quartz.SchedulerException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/29.
 */
@Path("/pm25")
public class Pm25Resource extends BaseResource {

    public static final String Tag = "Pm25";

    public static final String cron = "0 0 8-20 * * ?";


    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {

        try {
            QuartzHelper.addSingleJob(Pm25Resource.Tag,cron,Pm25Job.class);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return super.onServiceCreate(resp, sid, apiSecret, title, sData, cData);
    }

    @Override
    protected String getTag() {
        return Tag;
    }
}
