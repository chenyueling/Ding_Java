package cn.jpush.alertme.factory.plugins.finance.gold;

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
 * Created by chenyueling on 2015/1/7.
 */
@Path("/gold")
public class GoldTipResource extends BaseResource {

    public static final String Tag = "GoldTip";
    private static final Logger Log = LoggerFactory.getLogger(GoldTipResource.class);
    private static final String cron = "0 0 10 ? * 1-5";

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        try {
            Log.info("[GoldTipResource onServiceCreate] add task cron express :" + cron);
            QuartzHelper.addSingleJob(GoldTipResource.Tag,cron,GoldTipJob.class);
        } catch (SchedulerException e) {
            Log.info("[GoldTipResource onServiceCreate] add task exception" + e.toString());
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
