package cn.jpush.alertme.factory.plugins.tudou;

import cn.jpush.alertme.factory.common.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by chenyueling on 2015/1/21.
 */
public class Post {

    private static final String  PUSHED_POST = "s:Tudou:pushed.set";

    public static boolean isPushed(String id){

        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            return jedis.sismember(PUSHED_POST, id);
        } catch (JedisConnectionException e) {
            RedisFactory.release(jedis, true);
            throw e;
        } finally {
            RedisFactory.release(jedis);
        }
    }


    public static void addPushSet(String id) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            jedis.sadd(PUSHED_POST, id);
        } catch (JedisConnectionException e) {
            RedisFactory.release(jedis, true);
            throw e;
        } finally {
            RedisFactory.release(jedis);
        }
    }
}
