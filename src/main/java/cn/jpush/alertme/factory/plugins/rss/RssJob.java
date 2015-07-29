package cn.jpush.alertme.factory.plugins.rss;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/27.
 */
public class RssJob implements Job {

    private static final String RSS_URL = "feed";

    private static final Logger Log = LoggerFactory.getLogger(RssJob.class);


    private ServiceDao serviceDao;
    private ClientServiceDao clientServiceDao;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);


        String sid = null;

        String feed = null;
        String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        Log.debug("[RssJob execute] jobName " + jobName);
        Service service = serviceDao.findById(jobName);
        sid = service.getId();
        List<ClientService> clientServices = clientServiceDao.findBySid(sid);
        for (ClientService clientService : clientServices) {
            String cid = clientService.getId();
            Map<String, String> sc_data = clientService.getMergeData();
            feed = sc_data.get(RssJob.RSS_URL);
            try {
                SyndEntry syndEntry = RssUtils.RssFirstArticle(feed);
                String link = syndEntry.getLink();
                String title = syndEntry.getTitle();

                if (Post.isPushed(link, cid) == true) {
                    continue;
                }
                Article article = new Article();
                article.setLink(link);
                article.setTitle(title);
                AlertMeClient.pushByCid(cid).setArticle(article).send();
                Post.addPushSet(link, cid);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (FeedException e) {
                e.printStackTrace();
            } catch (HttpRequestException e) {
                e.printStackTrace();
            }
        }
    }
}
