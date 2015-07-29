package cn.jpush.alertme.factory.plugins.weixin.re;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import cn.jpush.alertme.factory.util.LogUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/2/5.
 */
public class WeixinReJob implements Job {
    public static final String SC_TYPE = "type";

    public ServiceDao serviceDao;
    public ClientServiceDao clientServiceDao;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Service> services = serviceDao.findByTag(WeixinReResource.Tag);
        for (Service service : services) {
            String sid = service.getId();
            List<ClientService> clientServices = clientServiceDao.findBySid(sid);
            for (ClientService clientService : clientServices) {
                String cid = clientService.getId();
                Map<String, String> sc_data = clientService.getMergeData();
                String type = sc_data.get(WeixinReJob.SC_TYPE);
                int type_i = Integer.parseInt(type);
                try {
                    List<Map<String, String>> analysis = WeixinReUtils.analysis(type_i);
                    Map<String, String> map = analysis.get(0);
                    String title = map.get(WeixinReUtils.TITLE);
                    String link = map.get(WeixinReUtils.LINK);

                    if(Post.isPushed(link,cid) == true){
                        continue;
                    }

                    Article article = new Article();
                    article.setLink(link);
                    article.setLink(title);

                    AlertMeClient.pushByCid(cid).setArticle(article).send();

                    Post.addPushSet(link,cid);

                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e(String.format("[WexinReJob execute] clientService execude exception type %s", type));
                    continue;
                } catch (HttpRequestException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public WeixinReJob() {
        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
    }
}
