package cn.jpush.alertme.factory.plugins.weather;

import cn.jpush.alertme.factory.common.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by chenyueling on 2014/12/25.
 */
public class Post {
    private static final String PUSHED_POST = "s:Weather:%s:pushed.set";

    public static boolean isPushed(String tag, String cid) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.sismember(String.format(PUSHED_POST, cid), tag);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void addPushSet(String tag, String cid) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.sadd(String.format(PUSHED_POST, cid), tag);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }
}
