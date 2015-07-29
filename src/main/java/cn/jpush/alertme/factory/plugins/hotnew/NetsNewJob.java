package cn.jpush.alertme.factory.plugins.hotnew;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/22.
 */
public class NetsNewJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            List<Map<String, String>> hotNews = NetsNewUtils.catchHotNew();
            Map<String, String> news = hotNews.get(0);
            if (news != null) {
                String link = news.get(NetsNewUtils.LINK);
                String title = news.get(NetsNewUtils.TITLE);
                Article article = new Article();
                article.setTitle(title);
                article.setLink(link);

                if (Post.isPushed(link)) {
                    return;
                }

                AlertMeClient.pushByTag(NetsNewResource.Tag).setArticle(article).send();

                Post.addPushSet(link);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
