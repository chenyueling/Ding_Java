package cn.jpush.alertme.factory.common;

import cn.jpush.alertme.factory.util.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Date;
import java.util.Map;

/**
 * Redis Common Method
 * Created by ZeFanXie on 14-12-24.
 */
public class RedisHelper {
    public static String PUSH_SET_TEMPLATE = "s:{tag}.push.set";
    public static String ARTICLE_KEY = "h:{tag}.push.set:{id}";
    public static String LATEST_PUSH_TIME_TEMPLATE = "s:{tag}.latest.push.time";


    public static boolean inPushSet(String tag, String id) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.sismember(PUSH_SET_TEMPLATE.replace("{tag}", tag), id);
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
            client.sadd(PUSH_SET_TEMPLATE.replace("{tag}", tag), id);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    /**
     * 存储文章业务方法
     *
     * @param articleMap
     */

    public static void saveArticle(String tag, String id, Map<String, String> articleMap) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            String article_key = ARTICLE_KEY.replace("{tag}", tag).replace("{id}", id);
            jedis.hmset(article_key, articleMap);
        } catch (JedisConnectionException e) {
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
    }

    /**
     * 获取文章
     *
     * @param tag
     * @param id
     * @return articleMapStr
     */
    public static Map<String, String> getArticle(String tag, String id) {
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getInstance();
            String article_key = ARTICLE_KEY.replace("{tag}", tag).replace("{id}", id);
            return jedis.hgetAll(article_key);
        } catch (JedisConnectionException e) {
            RedisFactory.release(jedis, true);
        } finally {
            RedisFactory.release(jedis);
        }
        return null;
    }


    public static void setLaterPushTime(String tag, Date date) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.set(LATEST_PUSH_TIME_TEMPLATE.replace("{tag}", tag), date.getTime() + "");
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void setLaterPushTime(String tag) {
        setLaterPushTime(tag, new Date());
    }

    public static Date getLaterPushTime(String tag) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            String date = client.get(LATEST_PUSH_TIME_TEMPLATE.replace("{tag}", tag));
            if (!StringUtil.isEmpty(date)) {
                return new Date(Long.parseLong(date));
            } else {
                return null;
            }
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }
}
