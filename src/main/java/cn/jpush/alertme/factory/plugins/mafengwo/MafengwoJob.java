package cn.jpush.alertme.factory.plugins.mafengwo;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.Service;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/29.
 */
public class MafengwoJob implements Job {


    private ServiceDao serviceDao;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Service> services = serviceDao.findByTag(MafengwoResource.Tag);

        try {
            List<Map<String, String>> hots = MafengwoUtils.getHot();
            int i = 0;

            String link = hots.get(i).get(MafengwoUtils.LINK);
            String title = hots.get(i).get(MafengwoUtils.TITLE);
            while (Post.isPushed(link) && i < hots.size()) {
                i++;
                link = hots.get(i).get(MafengwoUtils.LINK);
                title = hots.get(i).get(MafengwoUtils.TITLE);
            }

            if (Post.isPushed(link) == true) {
                return;
            }

            Article article = new Article();
            article.setLink(link);
            article.setTitle(title);

            AlertMeClient.pushByTag(MafengwoResource.Tag).setArticle(article).send();

            Post.addPushSet(link);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }

        AlertMeClient.pushByTag(MafengwoResource.Tag);

    }


    public MafengwoJob() {
        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
    }
}
