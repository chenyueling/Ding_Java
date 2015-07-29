package cn.jpush.alertme.factory.plugins.mafengwo;

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
@Path("/mafengwo")
public class MafengwoResource extends BaseResource {

    public static final String Tag = "Mafengwo";
    private static final String cron = "0 25 20 * * ?";

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        try {
            QuartzHelper.addSingleJob(MafengwoResource.Tag,cron,MafengwoJob.class);
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
