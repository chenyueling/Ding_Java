package cn.jpush.alertme.factory.dao;

import cn.jpush.alertme.factory.common.RedisFactory;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储失效的ClientService, 当ClientService推送失败的时候(返回403), 
 * 将被存储在此,以供优化 
 * Created by ZeFanXie on 15-1-27.
 */
public class FailureServiceStore {
    private static final String FAILURE_SERVICE_LIST_KEY = "l:failure.service.list";
    private static final String FAILURE_SERVICE_KEY = "h:failure.service:{cid}";
    
    public static void addFaulureService(ClientService clientService) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            
            String key = FAILURE_SERVICE_KEY.replace("{cid}", clientService.getId());
            if (client.exists(key)) {
                client.hincrBy(key, "times", 1);
            } else {
                Map<String, String> saveMap = new HashMap<>();
                Service service = clientService.getService();
                saveMap.put("id", clientService.getId());
                saveMap.put("sid", service.getId());
                saveMap.put("title", service.getTitle());
                saveMap.put("tag", service.getTag());
                saveMap.put("api_secret", service.getApiSecret());
                client.hmset(FAILURE_SERVICE_KEY.replace("{cid}", clientService.getId()), saveMap);
                client.hincrBy(key, "times", 1);
                client.rpush(FAILURE_SERVICE_LIST_KEY, clientService.getId());
            }
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }
    
    public static List<Map<String, String>> getFailureServices(int start, int row) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            List<String> ids = client.lrange(FAILURE_SERVICE_LIST_KEY, start, start + row);
            List<Map<String, String>> result = new ArrayList<>();
            for (String id : ids) {
                String key = FAILURE_SERVICE_KEY.replace("{cid}", id);
                if (client.exists(key)) {
                    result.add(client.hgetAll(key));
                }
            }
            return result;
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
                
    }
    
    public static void removeFailureService(String cid) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            String key = FAILURE_SERVICE_KEY.replace("{cid}", cid);
            if (client.exists(key)) {
                client.del(key);
            }
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }


}
