package cn.jpush.alertme.factory.plugins.pm25;

import cn.jpush.alertme.factory.common.RedisFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by chenyueling on 2015/1/30.
 */
public class Post {
    private static final String LAST_STAMP = "i:PM25:%s:last_stamp";
    private static final String TODAY_LEVEL = "i:PM25:%s:today_level";

    public static String getLastStamp(String cid) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            String lastStamp = jedis.get(String.format(LAST_STAMP, cid));
            if (lastStamp == null) {
                return "0";
            }
            return lastStamp;
        } catch (Exception e) {
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
        return "0";
    }

    public static void setStamp(String cid, long stamp) {
        Jedis jedis = RedisFactory.getInstance();
        try {
            jedis.set(String.format(LAST_STAMP, cid), stamp + "");
        } catch (Exception e) {
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
    }

    public static String getTodayLevel(String cid) {
        Jedis jedis = RedisFactory.getInstance();
        try {
            String todayLevel = jedis.get(String.format(TODAY_LEVEL, cid));
            if(todayLevel == null){
                return "0";
            }
            return todayLevel;
        } catch (Exception e) {
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
        return null;
    }

    public static void setTodayLevel(String cid, String todayLevel) {
        Jedis jedis = RedisFactory.getInstance();
        try {
            jedis.set(String.format(LAST_STAMP, cid), todayLevel);
        } catch (Exception e) {
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
    }

}
