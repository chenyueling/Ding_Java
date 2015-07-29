package cn.jpush.alertme.factory.plugins.eboic;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
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
 * Created by chenyueling on 2015/1/22.
 */
public class EboicLotteryJob implements Job {


    public ServiceDao serviceDao;

    public ClientServiceDao clientServiceDao;

    /**
     * "sevenHappyColor" :
     * "fucai3D" :
     * "superLotto":
     * "doubleChromosphere":
     */
    private static final String LOTTERY_TYPE = "type";


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
            clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);

            List<Service> services = serviceDao.findByTag(EboicLotteryResource.Tag);

            for (Service service : services) {
                String sid = service.getId();
                List<ClientService> clientServices = clientServiceDao.findBySid(sid);
                String cid = null;
                for (ClientService clientService : clientServices) {
                    cid = clientService.getId();
                    Map<String, String> sc_data = clientService.getMergeData();
                    String type = sc_data.get(EboicLotteryJob.LOTTERY_TYPE);
                    EboicLotteryUtils.Lotterys.Lottery lottery = EboicLotteryUtils.getLotteryByType(type);
                    if (lottery != null) {

                        if (Post.isPushed(lottery.expect, type) == true) {
                            return;
                        }

                        Text text = new Text();
                        text.setContent(lottery.toString());

                        AlertMeClient.pushByCid(cid).setText(text).send();

                        Post.addPushSet(lottery.expect, type);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

}
