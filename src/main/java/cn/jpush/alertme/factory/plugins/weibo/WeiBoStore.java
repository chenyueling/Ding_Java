package cn.jpush.alertme.factory.plugins.weibo;

import cn.jpush.alertme.factory.common.RedisFactory;
import cn.jpush.alertme.factory.util.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Set;

/**
 * WeiBo DataBase Util
 * Created by ZeFanXie on 14-12-30.
 */
public class WeiBoStore {
    private static final String AUTO_ACCESS_TOKEN = "2.00DJCMuFeTY73Ecf187a9916A4NckB";
    private static final String ACCESS_TOKEN_KEY = "i:weibo:auto.access.token";
    // 需要处理的User Id列表
    private static final String FOCUS_USER_SET_KEY = "s:weibo:focus.user.id";
    // User Id 与 Client Id关联列表
    private static final String FOCUS_USER_REF_KEY = "s:weibo:focus.user.ref:{uid}";
    // Cid关联的UserId
    private static final String CID_TO_USER_REF_KEY = "i:cid.to.user.ref:{cid}";

    public static String findAutoAccessToken() {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            String accessToken = client.get(ACCESS_TOKEN_KEY);
            if (StringUtil.isEmpty(accessToken)) {
                return AUTO_ACCESS_TOKEN;
            } else {
                return ACCESS_TOKEN_KEY;
            }
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static Set<String> findCidByUserId(String uid) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.smembers(FOCUS_USER_REF_KEY.replace("{uid}", uid));
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static Set<String> findFocusUserId() {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            return client.smembers(FOCUS_USER_SET_KEY);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void addFocusUserId(String... userIds) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.sadd(FOCUS_USER_SET_KEY, userIds);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void addCidToUserId(String uid, String cid) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.sadd(FOCUS_USER_REF_KEY.replace("{uid}", uid), cid);
            client.set(CID_TO_USER_REF_KEY.replace("{cid}", cid), uid);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }


}