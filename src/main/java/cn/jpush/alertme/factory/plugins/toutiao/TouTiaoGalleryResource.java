package cn.jpush.alertme.factory.plugins.toutiao;

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
@Path("/toutiao_gallery")
public class TouTiaoGalleryResource extends BaseResource {
    private static final Logger Log = LoggerFactory.getLogger(TouTiaoGalleryResource.class);

    public static final String Tag = "TouTiaoGallery";

    private static final String cron = "0 15 17 *  * ?";

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        Log.info("[TouTiaoGalleryResource onServiceCreate] add SingleJob cron express :" + cron);
        try {
            QuartzHelper.addSingleJob(Tag, cron, TouTiaoGalleryJob.class);
        } catch (SchedulerException e) {
            e.printStackTrace();
            Log.error("[TouTiaoGalleryResource onServiceCreate] add SingleJob error :" + e.toString());
        }
        return super.onServiceCreate(resp, sid, apiSecret, title, sData, cData);
    }


    @Override
    protected boolean onFollowedChange(HttpServletResponse resp, String sid, String cid, Long followed) {
        try {
            QuartzHelper.addSingleJob(Tag, cron, TouTiaoGalleryJob.class);
        } catch (SchedulerException e) {
            e.printStackTrace();
            Log.error("[TouTiaoGalleryResource onServiceCreate] add SingleJob error :" + e.toString());
        }
        return super.onFollowedChange(resp, sid, cid, followed);
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
