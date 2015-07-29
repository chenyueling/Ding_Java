package cn.jpush.alertme.factory.plugins.weather;

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
 * Created by chenyueling on 2014/12/25.
 */
@Path("/weather")
public class WeatherResource extends BaseResource {
    private static final Logger Log = LoggerFactory.getLogger(WeatherUtils.class);
    public static final String Tag = "Weather";
    private static final String cron = "0 30 20 * *  ?";
    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        try {
            QuartzHelper.addSingleJob(Tag,cron,WeatherJob.class);
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
