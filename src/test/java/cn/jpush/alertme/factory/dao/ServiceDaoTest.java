package cn.jpush.alertme.factory.dao;

import cn.jpush.alertme.factory.model.Service;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import cn.jpush.alertme.factory.util.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service Dao Unit Test
 * Created by ZeFanXie on 14-12-17.
 */
public class ServiceDaoTest {
    private static ServiceDao serviceDao;

    @BeforeClass
    public static void beforeClass() {
        serviceDao = TestUtil.getSpringBean(ServiceDao.class, "serviceDao");
        initData();
    }


    public static void initData() {
        Service service = serviceDao.findById("1000");
        if (service == null) {
            Map<String, String> data = new HashMap<>();
            data.put("key", "value");
            service = new Service();
            service.setId("1000");
            service.setApiSecret(StringUtil.getRandomNum(24));
            service.setTitle("Test Title");
            service.setData(data);
            service.setTag("Test Tag");
            serviceDao.save(service);
        }
    }

    @Test
    public void save() {
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");
        Service service = new Service();
        service.setId(StringUtil.getRandomNum(4) + "");
        service.setApiSecret(StringUtil.getRandomNum(24));
        service.setTitle("Test Title");
        service.setData(data);
        service.setTag("Test Tag");
        serviceDao.save(service);
    }

    @Test
    public void findById() {
        Service service = serviceDao.findById("1000");
        System.out.printf(JsonUtil.toJson(service));
    }

    @Test
    public void update() {
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        Service s = serviceDao.findById("1000");
        s.setTitle(s.getTitle() + "-update");
        s.setData(data);
        serviceDao.update(s);
    }

    @Test
    public void findByTag() {
        List<Service> list = serviceDao.findByTag("Test Tag");
        System.out.println(JsonUtil.toJson(list));
    }


}
