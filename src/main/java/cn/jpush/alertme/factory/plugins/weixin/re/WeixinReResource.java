package cn.jpush.alertme.factory.plugins.weixin.re;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import cn.jpush.alertme.factory.plugins.youku.YoukuJob;
import org.quartz.SchedulerException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/2/5.
 */
@Path("weixinre")
public abstract class WeixinReResource extends BaseResource {

    public static final String Tag = "WeiXinRe";
    public static final String cron = "0 25 19 * * ?";


    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        try {
            QuartzHelper.addSingleJob(WeixinReResource.Tag, cron, WeixinReJob.class);
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
