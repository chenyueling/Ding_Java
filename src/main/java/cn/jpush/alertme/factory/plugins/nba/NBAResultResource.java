package cn.jpush.alertme.factory.plugins.nba;

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
 * NBA比赛结果提醒
 * Created by chenyueling on 2014/12/22.
 */
@Path("/nba_result")
public class NBAResultResource extends BaseResource {

    private static final Logger Log = LoggerFactory.getLogger(NBAResultResource.class);
    public static final String Tag = "NBAResult";
    // 两分钟执行一次
    private static final String cron = "0 */2 * * * ?";

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        //QuartzHelper.addSingleJob();
        Log.info("[NBAResultResource onServiceCreate] add SingleJob cron express :"+ cron);
        try {
            QuartzHelper.addSingleJob(Tag,cron,NBAResultJob.class);
        } catch (SchedulerException e) {
            e.printStackTrace();
            Log.error("[NBAResultResource onServiceCreate] add SingleJob error :" + e.toString() );
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
