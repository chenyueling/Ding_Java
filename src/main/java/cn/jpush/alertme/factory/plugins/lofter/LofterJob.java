package cn.jpush.alertme.factory.plugins.lofter;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2014/12/24.
 */
public class LofterJob implements Job {


    private ServiceDao serviceDao = null;
    private ClientServiceDao clientServiceDao = null;

    private static final String C_TAG = "tag";

    private static final String CID = "cid";

    private static final String ARTICLE = "article";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);

        List<Map<String, Object>> pushList = new ArrayList<>();

        List<Service> services = serviceDao.findByTag(LofterResource.Tag);
        for (Service service : services) {
            Map<String, String> s_data = service.getData();
            String sid = service.getId();

            List<ClientService> clientServices = clientServiceDao.findBySid(sid);
            for (ClientService clientService : clientServices) {
                Map<String, String> c_data = clientService.getData();
                String tag = c_data.get(LofterJob.C_TAG);
                List<Map<String, String>> articlesByTag = LofterUtils.getArticleByTag(tag);
                String title = articlesByTag.get(0).get(LofterUtils.TITLE);
                String link = articlesByTag.get(0).get(LofterUtils.LINK);

                boolean isPushed = Post.isPushed(link, service.getId());

                if (isPushed == true) {
                    continue;
                }

                Article article = new Article();

                article.setLink(link);
                article.setTitle(title);

                Map<String, Object> map = new HashMap<>();
                map.put(LofterJob.CID, clientService.getId());
                map.put(LofterJob.ARTICLE, article);

                pushList.add(map);
            }
        }


        for (Map<String, Object> map : pushList) {
            boolean isPushed = Post.isPushed(((Article) map.get(LofterJob.ARTICLE)).getLink(), (String) map.get(LofterJob.CID));
            if (isPushed == true) {
                continue;
            }
            try {
                //push
                AlertMeClient.pushByCid((String) map.get(LofterJob.CID)).setArticle((Article) map.get(LofterJob.ARTICLE)).send();
                //add tag
                Post.addPushSet(((Article) map.get(LofterJob.ARTICLE)).getLink(), (String) map.get(LofterJob.CID));
            } catch (HttpRequestException e) {
                e.printStackTrace();
            }
        }
    }
}
