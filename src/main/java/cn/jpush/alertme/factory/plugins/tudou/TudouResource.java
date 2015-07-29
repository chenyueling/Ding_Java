package cn.jpush.alertme.factory.plugins.tudou;

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
 * Created by chenyueling on 2015/1/21.
 */
@Path("/tudou")
public class TudouResource extends BaseResource {
    private static final Logger Log = LoggerFactory.getLogger(TudouResource.class);
    public static final String Tag = "Tudou";

    private static final String cron = "0 0/10 * * * ? ";


    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        try {
            QuartzHelper.addSingleJob(TudouResource.Tag,cron,TudouJob.class );
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
