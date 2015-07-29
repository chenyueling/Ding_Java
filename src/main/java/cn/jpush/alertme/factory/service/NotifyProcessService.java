package cn.jpush.alertme.factory.service;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Process Ding Service Request
 * Created by ZeFanXie on 14-12-17.
 */
@org.springframework.stereotype.Service
public class NotifyProcessService {

    @Resource
    private ServiceDao serviceDao;
    @Resource
    private ClientServiceDao clientServiceDao;

    public void saveService(String sid,
                            String apiSecret,
                            String title,
                            Map<String, String> sData,
                            List<BaseResource.CData> cData,
                            String tag) {
        Service service = new Service();
        service.setId(sid);
        service.setApiSecret(apiSecret);
        service.setTitle(title);
        service.setData(sData);
        service.setTag(tag);
        serviceDao.save(service);

        for (BaseResource.CData c : cData) {
            ClientService cs = new ClientService();
            cs.setId(c.cid);
            cs.setService(service);
            cs.setData(c.getData());
            clientServiceDao.save(cs);
        }
    }

    public void updateService(String sid, String title, Map<String, String> sData, List<BaseResource.CData> cData) {
        Service service = serviceDao.findById(sid);

        if (service != null) {
            service.setTitle(title);
            service.setData(sData);

            for (BaseResource.CData c : cData) {
                ClientService cs = clientServiceDao.findById(c.cid);
                if (cs != null) {
                    cs.setData(c.getData());
                    clientServiceDao.update(cs);
                } else {
                    cs = new ClientService();
                    cs.setId(c.cid);
                    cs.setService(service);
                    cs.setData(c.getData());
                    clientServiceDao.save(cs);
                }
            }
        } else {
            // TODO log error message
        }


    }

    public void saveClientService(String sid, String cid, Map<String, String> data) {
        Service service = serviceDao.findById(sid);

        if (service != null) {
            ClientService cs = new ClientService();
            cs.setId(cid);
            cs.setService(service);
            cs.setData(data);
            clientServiceDao.save(cs);
        } else {
            // TODO log error message
        }
    }

    

}
