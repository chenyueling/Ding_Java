package cn.jpush.alertme.factory.plugins.wechat100;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.ws.rs.Path;
import java.io.IOException;
import java.util.List;

/**
 * Created by chenyueling on 2015/2/6.
 */
public class Wechat100Job implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        try {
            List<Wechat100Utils.Item> items = Wechat100Utils.get24Hot();
            Wechat100Utils.Item item = null;
            String link = items.get(0).getLink();
            String title = null;
            int i = 0;
            while (Post.isPushed(link) && i < items.size()) {
                i++;
                item = items.get(i);
                link = item.getLink();
            }
            if (item == null || i >= items.size()) {
                return;
            }

            title = item.title;
            link = item.getLink();

            Article article = new Article();
            article.setLink(link);
            article.setTitle(title);

            AlertMeClient.pushByTag(Wechat100Resource.Tag).setArticle(article).send();

            Post.addPushSet(link);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
    }


}
