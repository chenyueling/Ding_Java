package cn.jpush.alertme.factory.plugins.eboic;

import cn.jpush.alertme.factory.common.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by chenyueling on 2015/1/22.
 */
public class Post {
    private static final String PUSHED_POST = "s:EboicLottery:%s:pushed.set";

    public static boolean isPushed(String expect,String type){

        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.sismember(String.format(PUSHED_POST,type), expect);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }


    public static void addPushSet(String expect,String type) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.sadd(String.format(PUSHED_POST,type), expect);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }
}
