package cn.jpush.alertme.factory.plugins.eboic;

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
@Path("/lottery")
public class EboicLotteryResource extends BaseResource {

    private static final Logger Log = LoggerFactory.getLogger(EboicLotteryResource.class);

    public static final String Tag = "EboicLottery";
    //   0 0/5 19-23 * * ? *
    //每天晚上七点开始,时隔五分钟一次轮询
    private static final String cron = "0 0/5 19-23 * * ? *";


    @Override
    protected boolean onClientServiceCreate(HttpServletResponse resp, String sid, String cid, Map<String, String> data) {
        Log.info("[EboicLotteryResource onClientServiceCreate]  add SingleJob cron express :" + cron);
        try {
            QuartzHelper.addSingleJob(EboicLotteryResource.Tag,cron,EboicLotteryJob.class);
        } catch (SchedulerException e) {
            e.printStackTrace();
            Log.error("[EboicLotteryResource onServiceCreate] add SingleJob error :" + e.toString());
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
