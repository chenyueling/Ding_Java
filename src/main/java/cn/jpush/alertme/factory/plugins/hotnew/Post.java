package cn.jpush.alertme.factory.plugins.hotnew;

import cn.jpush.alertme.factory.common.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by chenyueling on 2015/1/22.
 */
public class Post {
    private static final String PUSHED_POST = "s:NetsNew:pushed.set";

    public static boolean isPushed(String url){

        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.sismember(PUSHED_POST, url);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }


    public static void addPushSet(String url) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.sadd(PUSHED_POST, url);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }
}
