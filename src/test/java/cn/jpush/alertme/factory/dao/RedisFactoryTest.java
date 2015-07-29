package cn.jpush.alertme.factory.dao;

import cn.jpush.alertme.factory.common.RedisFactory;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by ZeFanXie on 14-12-18.
 */
public class RedisFactoryTest {

    @Test
    public void getCollection() {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.set("test", "test");
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            client = null;
        } finally {
            RedisFactory.release(client);
        }

    }
}
