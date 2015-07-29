package cn.jpush.alertme.factory.common;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.util.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Alert Me Client Test
 * Created by ZeFanXie on 14-12-19.
 */
public class AlertMeClientTest {

    @BeforeClass
    public static void beforeClass() {
        ServiceDao serviceDao = TestUtil.getSpringBean(ServiceDao.class, "serviceDao");
        ClientServiceDao clientServiceDao = TestUtil.getSpringBean(ClientServiceDao.class, "clientServiceDao");
        AlertMeClient.setTextResource(serviceDao, clientServiceDao);
    }


    @Test
    public void pushByTag() throws HttpRequestException {
        Text text = new Text("推送测试");
        AlertMeClient.pushByTag("LiWuShuo").setText(text).send();
    }

    @Test
    public void pushBySid() throws HttpRequestException {
        Text text = new Text("推送测试");
        AlertMeClient.pushBySid("0558").setText(text).send();
    }

    @Test
    public void pushByCid() throws HttpRequestException {
        Text text = new Text("推送测试");
        AlertMeClient.pushByCid("4e9efbb5-c2c0-4ca3-8b10-5c64f1832fda").setText(text).send();
    }
}
