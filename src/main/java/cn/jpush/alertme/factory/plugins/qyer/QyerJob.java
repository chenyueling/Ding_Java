package cn.jpush.alertme.factory.plugins.qyer;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

/**
 * Created by chenyueling on 2015/1/30.
 */
public class QyerJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        try {
            QyerUtils.Qyers qyers = QyerUtils.getQyers();
            QyerUtils.Qyers.Qyer qyer = qyers.data.get(0);
            String title = qyer.title;
            String link = QyerUtils.detail.replace(QyerUtils.Qyer_ID, qyer.id);
            if (Post.isPushed(link) == true){
                return;
            }

            Article article = new Article();
            article.setTitle(title);
            article.setLink(link);
            AlertMeClient.pushByTag(QyerResource.Tag).setArticle(article).send();

            Post.addPushSet(link);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
    }
}
