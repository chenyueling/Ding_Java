package cn.jpush.alertme.factory.dao;

import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Client Service Unit Test
 * Created by ZeFanXie on 14-12-17.
 */
public class ClientServiceDaoTest {
    private static ClientServiceDao clientServiceDao;

    @BeforeClass
    public static void beforeClass() {
        clientServiceDao = TestUtil.getSpringBean(ClientServiceDao.class, "clientServiceDao");
        initData();
    }

    private static void initData() {
        ClientService cs = clientServiceDao.findById("4b461c4f-5b59-4a39-83d7-406257a270d4");
        if (cs == null) {
            Map<String, String> data = new HashMap<>();
            data.put("key", "value");
            cs = new ClientService();
            cs.setId("4b461c4f-5b59-4a39-83d7-406257a270d4");
            cs.setData(data);
            Service service = new Service();
            service.setId("1000");
            cs.setService(service);
            clientServiceDao.save(cs);
        }
    }


    @Test
    public void save() {
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");
        ClientService cs = new ClientService();
        cs.setId(UUID.randomUUID().toString());
        cs.setData(data);
        Service service = new Service();
        service.setId("1000");
        cs.setService(service);
        clientServiceDao.save(cs);
    }

    @Test
    public void findById() {
        ClientService cs = clientServiceDao.findById("4b461c4f-5b59-4a39-83d7-406257a270d4");
        System.out.println(JsonUtil.toJson(cs));
    }

    @Test
    public void findBySid() {
        List<ClientService> list = clientServiceDao.findBySid("1000");
        System.out.println(JsonUtil.toJson(list));
    }

    @Test
    public void findByTag() {
        List<ClientService> list = clientServiceDao.findByTag("ZuiMeiA");
        System.out.println(JsonUtil.toJson(list));
    }

    @Test
    public void update() {
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        ClientService cs = clientServiceDao.findById("4b461c4f-5b59-4a39-83d7-406257a270d4");
        cs.setData(data);
        clientServiceDao.update(cs);
    }

}
