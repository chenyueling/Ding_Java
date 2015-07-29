package cn.jpush.alertme.factory.plugins.constellation;

import cn.jpush.alertme.factory.common.Config;
import cn.jpush.alertme.factory.common.RedisHelper;
import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import cn.jpush.alertme.factory.util.StringUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/14.
 */
public class ConstellJob implements Job {

    private ServiceDao serviceDao = null;

    private ClientServiceDao clientServiceDao = null;

    private static final String SC_CONSTELLNAME = "constellName";

    private static final Logger Log = LoggerFactory.getLogger(ConstellJob.class);


    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Log.debug("[ConstellJob execute] execute job");
        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
        List<Service> services = serviceDao.findByTag(ConstellResource.Tag);

        for (Service service : services) {
            try {
                String sid = service.getId();
                Map<String, String> s_data = service.getData();
                List<ClientService> clientServices = clientServiceDao.findBySid(sid);
                for (ClientService clientService : clientServices) {
                    String cid = clientService.getId();
                    Log.debug(String.format("[ConstellJob execute] execute job sid %s  cid %s", sid, cid));

                    try {
                        Map<String, String> c_data = clientService.getData();
                        Map<String, String> sc_data = mergeSdataCdata(s_data, c_data);
                        String constellName = sc_data.get(SC_CONSTELLNAME);
                        ConstellUtil.Result.Fortune fortune = ConstellUtil.getConstellFortune(constellName);

                        StringBuilder sb = new StringBuilder();
                        String title = sb.append(fortune.name).append("今日运势综合").append(fortune.all).toString();


                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                        Map<String, String> data = new HashMap<>();
                        data.put("publishDate", simpleDateFormat.format(new Date()));
                        data.put("title", title);
                        data.put("content", fortune.toString());
                        //将文章对象存入数据库。
                        String ariticleId = saveArticle(data);

                        String link = Config.ALERT_ME_JAVA_HOST + ConstellResource.getArticlePath(ariticleId);

                        Article article = new Article();
                        article.setLink(link);
                        article.setTitle(title);
                        AlertMeClient.pushByCid(cid).setArticle(article).send();
                    } catch (Exception e) {
                        Log.error(String.format("[ConstellJob execute] clientService error sid : %s , id : %s info : %s", sid, cid, e.toString()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // QuartzHelper.add
            }
        }


    }


    /**
     * 保存文章map对象方法
     *
     * @param articleMap
     * @return
     */
    public String saveArticle(Map<String, String> articleMap) {
        String id = StringUtil.getRandomString(10);
        RedisHelper.saveArticle(ConstellResource.Tag, id, articleMap);
        return id;
    }

    private Map<String, String> mergeSdataCdata(Map<String, String> s_data, Map<String, String> c_data) {
        Map<String, String> sc_data = new HashMap<>();
        sc_data.putAll(s_data);
        sc_data.putAll(c_data);
        return sc_data;
    }
}
