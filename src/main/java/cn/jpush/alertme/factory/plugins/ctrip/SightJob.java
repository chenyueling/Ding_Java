package cn.jpush.alertme.factory.plugins.ctrip;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/12.
 */
public class SightJob implements Job {

    private Logger Log = LoggerFactory.getLogger(SightJob.class);
    private ServiceDao serviceDao = null;
    private ClientServiceDao clientServiceDao = null;

    private static final String C_DISTRICTID = "DistrictId";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
            clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);

            List<Service> services = serviceDao.findByTag(SightResource.Tag);
            Map<String, String> s_data = null;
            Map<String, String> c_data = null;
            Map<String, String> sc_data = null;
            for (Service service : services) {
                String sid = service.getId();
                s_data = service.getData();
                List<ClientService> clientServices = clientServiceDao.findBySid(sid);
                String cid = null;
                for (ClientService clientService : clientServices) {
                    try {
                        c_data = clientService.getData();
                        sc_data = mergeSdataCdata(s_data, c_data);
                        cid = clientService.getId();
                        int currentPush = Post.getCurrentPush(sid, cid);
                        /**
                         * 列表的长度是一定的，
                         * 第一次需要获取列表的长度，
                         * 进行记录，
                         * 这个就要保证携程网不对进行删除的情况下（这个需要考虑）
                         * 初始化数据完成后，从第一条开始推送，
                         * 每推送一次当前的推送的条数就需要加1
                         * 当一轮推送完成后，就需要从新更新条数 （为什么是一轮推送完，1->10，10,11）
                         */

                        String districtId = sc_data.get(SightJob.C_DISTRICTID);
                        SightUtils.Sights sights = SightUtils.getSights(districtId);

                        ArrayList<SightUtils.Sights.Sight> sightList = sights.Result;

                        int size = sightList.size();

                        /**
                         * 如果当前推送大于列表，那么证明一轮推送完成，则从第一条从新开始推送
                         */
                        if (currentPush >= size) {
                            currentPush = 0;
                            SightUtils.updateJson(districtId);
                        }

                        SightUtils.Sights.Sight sight = sightList.get(currentPush);
                        Article article = new Article();
                        String districtNameEncoder = URLEncoder.encode(sight.DistrictName, "utf-8");
                        String link = SightUtils.sightPoint.replace(SightUtils.DISTRICTID, sight.DistrictId).replace(SightUtils.SIGHTID, sight.SightId).replace(SightUtils.CITY_NAME, districtNameEncoder);
                        article.setLink(link);
                        article.setTitle(sight.Name);

                        AlertMeClient.pushByCid(cid).setArticle(article).send();
                        currentPush++;
                        Post.setCurrentPush(currentPush, sid, cid);
                    } catch (Exception e) {
                        Log.error("[SightJob execute] clientService id" + cid + " excute error " + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> mergeSdataCdata(Map<String, String> s_data, Map<String, String> c_data) {
        Map<String, String> sc_data = new HashMap<>();
        sc_data.putAll(s_data);
        sc_data.putAll(c_data);
        return sc_data;
    }
}
