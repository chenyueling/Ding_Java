package cn.jpush.alertme.factory.plugins.vehicles;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
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
 * Created by chenyueling on 2015/1/29.
 */
public class VehicleRestrictionJob implements Job {

    private ServiceDao serviceDao;
    private ClientServiceDao clientServiceDao;

    private static final String NUM = "num";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Service> services = serviceDao.findByTag(VehicleRestrictionResource.Tag);

        for (Service service : services) {
            String sid = service.getId();
            List<ClientService> clientServices = clientServiceDao.findBySid(sid);
            Map<String,String> sc_data;
            for (ClientService clientService : clientServices) {
                String cid = clientService.getId();
                sc_data = clientService.getMergeData();
                String num = sc_data.get(VehicleRestrictionJob.NUM);
                try {
                    if (VehicleRestrictionUtil.isLimited(num) == true){

                        Text text = new Text();
                        StringBuffer sb = new StringBuffer();
                        sb.append("明天车牌尾号为").append(num).append("的车辆限行");
                        text.setContent(sb.toString());
                        AlertMeClient.pushByCid(cid).setText(text).send();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HttpRequestException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public VehicleRestrictionJob() {
        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
    }
}
