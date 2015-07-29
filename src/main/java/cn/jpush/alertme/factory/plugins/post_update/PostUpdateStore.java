package cn.jpush.alertme.factory.plugins.post_update;

import cn.jpush.alertme.factory.common.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 *
 * Created by ZeFanXie on 15-1-26.
 */
public class PostUpdateStore {
    private static final String PUSHED_SET_KEY = "s:post.update.pushed.set:{tag}";

    public static boolean isPushable(String tag, String id) {
        return true;
    }

    public static boolean isPushed(String tag, String id) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.sismember(PUSHED_SET_KEY.replace("{tag}", tag), id);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void addPushSet(String tag, String id) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.sadd(PUSHED_SET_KEY.replace("{tag}", tag), id);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }
}
