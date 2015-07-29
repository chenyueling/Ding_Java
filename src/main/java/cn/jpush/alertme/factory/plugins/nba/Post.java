package cn.jpush.alertme.factory.plugins.nba;

import cn.jpush.alertme.factory.common.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by chenyueling on 2014/12/22.
 */
public class Post {

    private static final String PUSHED_POST = "s:NBAResult:pushed.set";

    public static boolean isPushed(String id){

        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.sismember(PUSHED_POST, id);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }


    public static void addPushSet(String id) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.sadd(PUSHED_POST, id);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }
}
