package cn.jpush.alertme.factory.plugins.imooc;

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

import java.util.Map;

/**
 * Imooc Spider Job
 * Created by ZeFanXie on 14-12-30.
 */
public class SpiderJob implements Job {
    private static final Logger Log = LoggerFactory.getLogger(SpiderJob.class);
    private static final String LIST_URL = "http://www.imooc.com/course/ajaxlist?pos_id=0&lange_id=0&is_easy=0&sort=last&pagesize=20&unlearn=0&page=1";
    private static final String POST_URL = "http://www.imooc.com/learn/{id}";
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Log.debug("Imooc Spider Execute....");

        try {
            String response = NativeHttpClient.get(LIST_URL);
            Map<String, String> post = format(response);
            if (post == null || StringUtil.isEmpty(post.get("id"))) {
                // TODO spider fail, report it
                return;
            }
            String id = post.get("id");
            boolean isPushed = RedisHelper.inPushSet(ImoocResource.Tag, id);
            if (isPushed) {
                Log.debug("Post was pushed, ignore");
                return;
            }

            // push
            Article article = new Article();
            article.setTitle(post.get("title"));
            article.setSummary(post.get("summary"));
            article.setLink(POST_URL.replace("{id}", id));
            AlertMeClient.pushByTag(ImoocResource.Tag).setArticle(article).send();

            RedisHelper.addPushSet(ImoocResource.Tag, id);
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }

    }

    private Map<String,String> format(String response) {
        try {
            JsonObject json = JsonUtil.format(response, JsonObject.class);
            JsonArray lists = json.getAsJsonArray("list");
            JsonObject item = lists.get(0).getAsJsonObject();
            return CollectionUtil.map().put("id",item.get("id").getAsString())
                    .put("title", item.get("name").getAsString())
                    .put("summary", item.get("short_description").getAsString())
                    .build();
        } catch (Exception e) {
            return null;
        }

    }
}
