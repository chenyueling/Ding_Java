package cn.jpush.alertme.factory.plugins.movie_download;

import cn.jpush.alertme.factory.common.RedisFactory;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Date;

/**
 * Movie download entity
 * Created by ZeFanXie on 14-12-25.
 */
public class Movie {
    private static final String MOVIE_KEY = "i:movie_download:{id}";

    private String id;
    private String title;
    private String fullTitle;
    private String link;
    private String content;
    private Long createTime;

    public Movie() {
    }

    public Movie(String id, String title, String fullTitle, String link, String content) {
        this.id = id;
        this.title = title;
        this.fullTitle = fullTitle;
        this.link = link;
        this.content = content;
        this.createTime = new Date().getTime();
    }

    public void save() {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            client.set(MOVIE_KEY.replace("{id}", this.id), JsonUtil.toJson(this));
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    /* static method */
    public static Movie findById(String id) {
        Jedis client = null;
        try {
            client = RedisFactory.getInstance();
            String dataStr = client.get(MOVIE_KEY.replace("{id}", id));
            if (!StringUtil.isEmpty(dataStr)) {
                return JsonUtil.format(dataStr, Movie.class);
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }
}
