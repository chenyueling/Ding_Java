package cn.jpush.alertme.factory.dao;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.model.PushMessage;
import cn.jpush.alertme.factory.plugins.weibo.SpiderJob;
import cn.jpush.alertme.factory.plugins.weibo.WeiBoStore;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by chenyueling on 2015/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class PushMessageDaoTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory = null;
    @Autowired
    private PushMessageDao pushMessageDao = null;

    @Test
    public void save() throws HttpRequestException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {


        
        PushMessage pushMessage = new PushMessage();
        pushMessage.setId(UUID.randomUUID().toString());
        pushMessage.setSid("69016698");
        pushMessage.setCid("1dd8e1a1-7a6d-4eaa-a845-180fff1b60e2");
        pushMessage.setType(AlertMeClient.MessageType.ARTICLE);
        pushMessage.setTag("tag");
        pushMessage.setPushTime(new Date());
        pushMessage.setTitle("title");
        pushMessage.setSummary("summary");
        pushMessage.setLink("link");
        pushMessage.setContent("content");


        SqlSession session = sqlSessionFactory.openSession();
        pushMessageDao = session.getMapper(PushMessageDao.class);
        pushMessageDao.setCharsetToUtf8mb4();
        session.commit();
        pushMessageDao.save(pushMessage);




    }

    

 /*   @Test
    public void findById(){
        PushMessage pushMessage = pushMessageDao.findById("21260cb9-ba35-45c9-85f4-65a77361b2d7");
        System.out.println(pushMessage.getLink());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        System.out.println(simpleDateFormat.format(pushMessage.getPushTime()));
    }

    @Test
    public void findByTag(){
        List<PushMessage> pushMessage = pushMessageDao.findByTag("Tudou");
        for (PushMessage messageRecord : pushMessage) {
            System.out.println(messageRecord.getLink());
        }
    }*/




}
