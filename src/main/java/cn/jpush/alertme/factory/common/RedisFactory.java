package cn.jpush.alertme.factory.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Redis Factory
 * Created by ZeFanXie on 14-12-18.
 */
public class RedisFactory {
    private static final Logger Log = LoggerFactory.getLogger(RedisFactory.class);

    private static JedisPool jedisPool = null;
    private static RedisFactory redisFactory = null;
    public static Jedis getInstance() {
        if (redisFactory ==  null) {
            redisFactory = new RedisFactory();
        }
        return redisFactory.getConnection();
    }

    private static synchronized void initPoll() {
        if (jedisPool == null) {
            Log.info("[initPool] Creating connection pool");
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Config.REDIS_POOL_MAX_TOTAL);
            config.setMaxIdle(Config.REDIS_POOL_MAX_IDLE);
            config.setMaxWaitMillis(Config.REDIS_POOL_MAX_WAIT_MILLIS);
            jedisPool = new JedisPool(config, Config.REDIS_SERVER_URL, Config.REDIS_SERVER_PORT);
            Log.info("[initPool] Success to create connection pool");
        }
    }

    private RedisFactory() {
        initPoll();
    }

    public Jedis getConnection() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisException e) {
            Log.error("Get Redis Collection Fail:" + e.getMessage());
            e.printStackTrace();
        }
        return jedis;
    }

    public static void release(Jedis jedis) {
        release(jedis, false);
    }

    public static void release(Jedis jedis, boolean isBroken) {
        if (jedis != null) {
            if (isBroken) {
                jedisPool.returnBrokenResource(jedis);
            } else {
                jedisPool.returnResource(jedis);
            }
        }
    }
}
