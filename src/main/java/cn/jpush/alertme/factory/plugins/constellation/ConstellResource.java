package cn.jpush.alertme.factory.plugins.constellation;

import cn.jpush.alertme.factory.common.ArticleResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/14.
 */
@Path("/constell")
public class ConstellResource extends ArticleResource {

    private static final Logger Log = LoggerFactory.getLogger(ConstellResource.class);
    public static final String Tag = "Constell";
    private static final String cron = "0 30 8 * * ?";

    private static final String GET_ART_PATH = "/constell";

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        Log.info(String.format("[ConstellResource onServiceCreate] add task job cron express %s", cron));
        try {
            QuartzHelper.addSingleJob(ConstellResource.Tag, cron, ConstellJob.class);
        } catch (SchedulerException e) {
            Log.error(String.format("[ConstellResource onServiceCreate] add task job fail exception %s", e.toString()));
            e.printStackTrace();
        }
        return super.onServiceCreate(resp, sid, apiSecret, title, sData, cData);
    }

    public static String getArticlePath(String id) {
        return GET_ART_PATH + ArticleResource.getArticlePath(id);
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
