package cn.jpush.alertme.factory.plugins.rss;

import cn.jpush.alertme.factory.common.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by chenyueling on 2015/1/27.
 */
public class Post {
    private static final String PUSHED_POST = "s:Rss:%s:pushed.set";

    public static boolean isPushed(String url,String cid) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.sismember(String.format(PUSHED_POST,cid), url);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void addPushSet(String url, String cid) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.sadd(String.format(PUSHED_POST,cid), url);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

}
