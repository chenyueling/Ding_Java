package cn.jpush.alertme.factory.plugins.tudou;

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

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/21.
 */
public class TudouJob implements Job {

    private ServiceDao serviceDao;
    private ClientServiceDao clientServiceDao;
    private static final String SC_ALBUMID = "albumId";


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
        List<Service> services = serviceDao.findByTag(TudouResource.Tag);
        for (Service service : services) {
            String sid = service.getId();
            List<ClientService> clientServices = clientServiceDao.findBySid(sid);
            for (ClientService clientService : clientServices) {
                String cid = clientService.getId();
                Map<String, String> sc_data = clientService.getMergeData();
                String aid = sc_data.get(SC_ALBUMID);
                try {
                    TudouUtils.Album albums  = TudouUtils.getAlbumById(aid);
                    int size= albums.items.size();
                    if (Post.isPushed(size - 1 + "") == true){
                        continue;
                    }
                    TudouUtils.Album.Item item = albums.items.get(size - 1);
                    Article article = new Article();
                    String link = TudouUtils.getPlayUrl(item.acode,item.icode);
                    String title = item.kw;
                    article.setLink(link);
                    article.setTitle(title);

                    AlertMeClient.pushByCid(cid).setArticle(article).send();
                    Post.addPushSet(size - 1 + "");

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HttpRequestException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
