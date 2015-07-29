package cn.jpush.alertme.factory.plugins.ctrip;

import cn.jpush.alertme.factory.common.RedisFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;


/**
 * Created by chenyueling on 2015/1/12.
 */
public class Post {

    private static final Logger Log = LoggerFactory.getLogger(Post.class);

    private static final String CURRENT_POST = "i:Sight:%s:%s:push.current";

    private static final String DISTRICT_JSON = "i:Sight:DISTRICT:%s:json";

    private static final String SIGHTS_TOTAL = "i:Sight:%s:%s:sigths.totalCount";

    public static void setCurrentPush(int currentIndex, String sid, String cid) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            jedis.set(String.format(CURRENT_POST, sid, cid), currentIndex + "");
        } catch (Exception e) {
            e.printStackTrace();
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
    }


    public static int getCurrentPush(String sid, String cid) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            String currentIndex = jedis.get(String.format(CURRENT_POST, sid, cid));
            if (currentIndex == null) {
                currentIndex = "0";
            }
            return Integer.parseInt(currentIndex);
        } catch (Exception e) {
            e.printStackTrace();
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
        return 0;
    }

    public static int getSightsTotalCount(String sid, String cid) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            return Integer.parseInt(jedis.get(String.format(SIGHTS_TOTAL, sid, cid)));
        } catch (Exception e) {
            e.printStackTrace();
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
        return 0;
    }

    public static void setSightsTotalCount(int totalCount, String sid, String cid) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            jedis.set(String.format(SIGHTS_TOTAL, sid, cid), totalCount + "");
        } catch (Exception e) {
            e.printStackTrace();
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
    }


    public static void setDistrictJsonCache(String districtId, String json) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            jedis.set(String.format(DISTRICT_JSON, districtId), json);
        } catch (Exception e) {
            e.printStackTrace();
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
    }

    public static String getDistrictJsonCache(String districtId) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            return jedis.get(String.format(DISTRICT_JSON, districtId));
        } catch (Exception e) {
            e.printStackTrace();
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
        return null;
    }
}
