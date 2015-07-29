package cn.jpush.alertme.factory.plugins.nba;

import cn.jpush.alertme.factory.common.Config;
import cn.jpush.alertme.factory.common.QuartzHelper;
import cn.jpush.alertme.factory.common.RedisHelper;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.util.StringUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2014/12/23.
 */
public class NBAGameTipJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            List<Map<Object, String>> games = NBAUtils.tomorrowNBAGame();
            String title = games.get(0).get(NBAUtils.GAME_TIME).substring(0, 10) + " 全部比赛\n";

            //create Html
            String textContent = "";
            Map<String, String> data = new HashMap<>();
            for (Map<Object, String> objectStringMap : games) {
                textContent = textContent + objectStringMap.get(NBAUtils.GAME_TIME).substring(11, 16) + "\t" + objectStringMap.get(NBAUtils.GAME_TEAM) + "</br>";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            data.put("publishDate", simpleDateFormat.format(new Date()));
            data.put("title", title);
            data.put("content", textContent);
            //将文章对象存入数据库。
            String ariticleId = saveArticle(data);

            Article article = new Article();
            article.setTitle(title);

            String link = Config.ALERT_ME_JAVA_HOST + NBAGameTipResource.getArticlePath(ariticleId);
            article.setLink(link);
            //push
            AlertMeClient.pushByTag(NBAGameTipResource.Tag).setArticle(article).send();

        } catch (IOException e) {
            e.printStackTrace();
            //当推送失败的时候需要在这里添加重试任务。
            try {
                QuartzHelper.addOneTimesJob(NBAGameTipResource.Tag, 2, this.getClass());
            } catch (SchedulerException e1) {
                e1.printStackTrace();
            }
        } catch (HttpRequestException e) {
            e.printStackTrace();
            //当推送失败的时候需要在这里添加重试任务。
            try {
                QuartzHelper.addOneTimesJob(NBAGameTipResource.Tag, 2, this.getClass());
            } catch (SchedulerException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 保存文章map对象方法
     *
     * @param articleMap
     * @return
     */
    public String saveArticle(Map<String, String> articleMap) {
        String id = StringUtil.getRandomString(10);
        RedisHelper.saveArticle(NBAGameTipResource.Tag, id, articleMap);
        return id;
    }

    public static void main(String[] args) throws IOException {
        List<Map<Object, String>> games = NBAUtils.tomorrowNBAGame();
        String title = games.get(0).get(NBAUtils.GAME_TIME).substring(0, 10) + " 全部比赛\n";

        //create Html
        String textContent = "";
        Map<String, Object> data = new HashMap<>();
        for (Map<Object, String> objectStringMap : games) {
            textContent = textContent + objectStringMap.get(NBAUtils.GAME_TIME).substring(11, 16) + "\t" + objectStringMap.get(NBAUtils.GAME_TEAM) + "</br>";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        data.put("publishDate", simpleDateFormat.format(new Date()));
        data.put("title", title);
        data.put("content", textContent);
        System.out.println(data);
    }
}
