package cn.jpush.alertme.factory.plugins.finance.bitcoin;

import cn.jpush.alertme.factory.common.RedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by chenyueling on 2015/1/7.
 */
public class Post {
    private static final String PUSHED_POST = "s:BitCoinTip:%s:%s:pushed.set";

    private static final String PUSH_PRICE = "i:BitCoinTip:%s:%s:pushed.price";

    public static boolean isPushed(String flag, String sid, String cid) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.sismember(String.format(PUSHED_POST, sid, cid), flag);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void setBaseLinePrice(String price,String sid,String cid) {
        Jedis jedis = null;
        try{
            jedis = RedisFactory.getInstance();
            jedis.set(String.format(PUSH_PRICE,sid,cid),price);
        }catch (JedisConnectionException e){
            RedisFactory.release(jedis, true);
            throw e;
        }finally {
            RedisFactory.release(jedis);
        }
    }

    public static String getBaseLinePrice(String sid,String cid){
        Jedis jedis = null;
        try{
            jedis = RedisFactory.getInstance();
            return jedis.get(String.format(PUSH_PRICE,sid,cid));
        }catch (JedisConnectionException e){
            RedisFactory.release(jedis, true);
            throw e;
        }finally {
            RedisFactory.release(jedis);
        }
    }

    public static void addPushSet(String flag, String sid, String cid) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.sadd(String.format(PUSHED_POST, sid, cid), flag);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }
}
