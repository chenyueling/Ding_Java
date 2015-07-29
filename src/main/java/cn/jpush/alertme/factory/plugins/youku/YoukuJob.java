package cn.jpush.alertme.factory.plugins.youku;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/2/6.
 */
public class YoukuJob implements Job {

    ServiceDao serviceDao;
    ClientServiceDao clientServiceDao;

    public static final String SC_VIDEO_ID = "video_id";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Service> services = serviceDao.findByTag(YoukuResource.Tag);
        for (Service service : services) {
            String sid = service.getId();
            List<ClientService> clientServices = clientServiceDao.findBySid(sid);
            for (ClientService clientService : clientServices) {
                String cid = clientService.getId();
                Map<String, String> sc_data = clientService.getMergeData();
                String video_id = sc_data.get(SC_VIDEO_ID);

                YoukuUtils.Video video = YoukuUtils.getLastUpdate(video_id);

                if (video == null) {
                    continue;
                }

                if (Post.isPushed(video.link, cid) == true) {
                    continue;
                }

                Article article = new Article();
                article.setTitle(video.title);
                article.setLink(video.link);

                try {

                    AlertMeClient.pushByCid(cid).setArticle(article).send();
                    Post.addPushSet(video.link, cid);
                } catch (HttpRequestException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public YoukuJob() {
        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
    }

}
