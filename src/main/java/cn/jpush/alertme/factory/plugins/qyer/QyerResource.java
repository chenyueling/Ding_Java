package cn.jpush.alertme.factory.plugins.qyer;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import org.quartz.SchedulerException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/30.
 */
@Path("qyer")
public class QyerResource extends BaseResource {

    public static final String Tag = "Qyer";

    private static final String cron = "0 30 10 * * ?";


    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        try {
            QuartzHelper.addSingleJob(QyerResource.Tag,cron,QyerJob.class);
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
