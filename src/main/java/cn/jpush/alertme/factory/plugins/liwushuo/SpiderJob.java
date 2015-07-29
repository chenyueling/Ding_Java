package cn.jpush.alertme.factory.plugins.liwushuo;

import cn.jpush.alertme.factory.common.RedisHelper;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.CollectionUtil;
import cn.jpush.alertme.factory.util.DateUtil;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Created by ZeFanXie on 14-12-18.
 */
public class SpiderJob implements Job{
    public static final Logger Log = LoggerFactory.getLogger(SpiderJob.class);
    public static final String REQUEST_URL = "http://api.liwushuo.com/v1/channels/1/items?offset=0&limit=1";
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Log.debug("LiWuShuo Spider Execute....");
        Date latestPushTime = RedisHelper.getLaterPushTime(LiWuShuoResource.Tag);
        if (latestPushTime != null && DateUtil.within(latestPushTime, new Date(), DateUtil.ONE_DATE / 2)) {
            Log.debug("Latest Push Time within half a date, Ignore");
            return;
        }
        try {
            String response = NativeHttpClient.get(REQUEST_URL);
            Map<String, String> post = format(response);
            String id = post.get("id");
            if (!StringUtil.isEmpty(id)) {
                boolean isPushed = RedisHelper.inPushSet(LiWuShuoResource.Tag, id);

                if (isPushed) {
                    return;
                }

                // push
                Article article = new Article();
                article.setTitle(post.get("title"));
                article.setLink(post.get("url"));
                AlertMeClient.pushByTag(LiWuShuoResource.Tag).setArticle(article).send();

                RedisHelper.addPushSet(LiWuShuoResource.Tag, id);
                RedisHelper.setLaterPushTime(LiWuShuoResource.Tag);
            }
        } catch (JsonSyntaxException e) {
            Log.error("[SpiderJob] Json Syntax Fail" + e.getMessage());
        } catch (HttpRequestException e) {
            e.printStackTrace();
            Log.error("[SpiderJob]" + e.toString());
        }
    }

    public Map<String, String> format(String response) {
        JsonObject json = JsonUtil.format(response, JsonObject.class);
        JsonObject data = json.getAsJsonObject("data");
        JsonArray items = data.getAsJsonArray("items");
        JsonObject item = items.get(0).getAsJsonObject();
        return CollectionUtil.map().put("title",item.get("title").getAsString())
                .put("url", item.get("url").getAsString())
                .put("id", item.get("id").getAsString())
                .build();
    }
}
