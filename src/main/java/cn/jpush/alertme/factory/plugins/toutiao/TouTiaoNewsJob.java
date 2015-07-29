package cn.jpush.alertme.factory.plugins.toutiao;

import cn.jpush.alertme.factory.common.QuartzHelper;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import java.util.Map;

/**
 * Created by chenyueling on 2014/12/24.
 */
public class TouTiaoNewsJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            Map<String,String> map = TouTiaoUtils.getTutiaoNews();
            String link = map.get(TouTiaoUtils.LINK);
            String title = map.get(TouTiaoUtils.TITLE);
            boolean isPushed = Post.isPushed(link,TouTiaoNewsResource.Tag);
            if(isPushed == true){
                return;
            }

            Article article = new Article();
            article.setTitle(title);
            article.setLink(link);

            AlertMeClient.pushByTag(TouTiaoNewsResource.Tag).setArticle(article).send();

            Post.addPushSet(link,TouTiaoNewsResource.Tag);

        } catch (HttpRequestException e) {
            e.printStackTrace();
            try {
                QuartzHelper.addOneTimesJob(TouTiaoNewsResource.Tag,2,this.getClass());
            } catch (SchedulerException e1) {
                e1.printStackTrace();
            }
        }
    }
}
