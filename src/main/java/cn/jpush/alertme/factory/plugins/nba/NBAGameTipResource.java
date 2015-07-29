package cn.jpush.alertme.factory.plugins.nba;

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
 * NBA  比赛提醒
 * Created by chenyueling on 2014/12/23.
 */
@Path("/nba_game_tip")
public class NBAGameTipResource extends ArticleResource{
    private static final Logger Log = LoggerFactory.getLogger(NBAGameTipResource.class);
    public static final String Tag = "NBAGameTip";

    private static final String GET_ART_PATH = "/nba_game_tip";


    private static final String cron = "0 0 21 * * ?";

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        try {
            Log.info("[NBAGameTipResource onServiceCreate] Job Create cron:" + cron);
            QuartzHelper.addSingleJob(NBAGameTipResource.Tag,cron,NBAGameTipJob.class);
        } catch (SchedulerException e) {
            Log.info("[NBAGameTipResource onServiceCreate] Job Create error :" + e.toString());
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
