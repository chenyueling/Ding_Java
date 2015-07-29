package cn.jpush.alertme.factory.plugins.zuimeia;

import cn.jpush.alertme.factory.common.RedisHelper;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.CollectionUtil;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * 最美应用 Spider
 * Created by ZeFanXie on 14-12-23.
 */
public class SpiderJob implements Job{
    private static final Logger Log = LoggerFactory.getLogger(SpiderJob.class);
    private static final String REQUEST_URL = "http://zuimeia.com/api/apps/app/?platform=2&sync_date={timestamp}";
    private static final String POST_URL = "http://zuimeia.com/app/{post_id}";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Log.debug("ZuiMeiA Spider Execute....");

        try {
            String response = NativeHttpClient.get(REQUEST_URL.replace("{timestamp}", (new Date().getTime() / 1000) + ""));
            Map<String, String> post = format(response);

            String id = post.get("id");
            if (!StringUtil.isEmpty(id)) {
                boolean isPushed = RedisHelper.inPushSet(ZuiMeiAResource.Tag, id);
                if (isPushed) {
                    return;
                }

                // push
                Article article = new Article();
                article.setTitle(post.get("title"));
                article.setSummary(post.get("sub_title"));
                article.setLink(POST_URL.replace("{post_id}", id));
                AlertMeClient.pushByTag(ZuiMeiAResource.Tag).setArticle(article).send();

                RedisHelper.addPushSet(ZuiMeiAResource.Tag, id);
            }

            System.out.println(JsonUtil.toJson(post));
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }

    }

    private Map<String, String> format(String response) {
        JsonObject json = JsonUtil.format(response, JsonObject.class);
        JsonArray data = json.getAsJsonArray("data");
        JsonObject firstItem = data.get(0).getAsJsonObject();
        return CollectionUtil.map()
                .put("id", firstItem.get("id").getAsString())
                .put("title", firstItem.get("title").getAsString())
                .put("sub_title", firstItem.get("sub_title").getAsString())
                .build();
    }

    public static void main(String[] args) throws JobExecutionException {
        new SpiderJob().execute(null);
    }

}
