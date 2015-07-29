package cn.jpush.alertme.factory.plugins.weibo;

import cn.jpush.alertme.factory.common.RedisHelper;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.CollectionUtil;
import cn.jpush.alertme.factory.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZeFanXie on 14-12-30.
 */
public class SpiderJob implements Job {
    private static final Logger Log = LoggerFactory.getLogger(SpiderJob.class);

    private static final String TIME_LIME_URL = "https://api.weibo.com/2/statuses/home_timeline.json?access_token={access_token}";
    private static final String PAGE_URL = "http://api.weibo.com/2/statuses/go?uid={user_id}&id={weibo_id}";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Log.debug(WeiBoResource.Tag + " Spider Execute....");

        try {
            Set<String> userIds = WeiBoStore.findFocusUserId();
            if (userIds.size() < 1) {
                Log.debug("Focus User Not Found, Ignore");
                return;
            }
            String response = NativeHttpClient.get(TIME_LIME_URL.replace("{access_token}", WeiBoStore.findAutoAccessToken()));
            List<Map<String, String>> result = formatResponse(response);

            for (Map<String, String> weibo : result) {
                String id = weibo.get("id");
                if (RedisHelper.inPushSet(WeiBoResource.Tag, id)) {
                   // 该消息已经推送过了
                   continue;
                }
                String userId = weibo.get("uid");
                if (!userIds.contains(userId)) {
                    //Log.debug(String.format("User:%s haven't follower, ignore", userId));
                    continue;
                }

                // push
                Article article = new Article();
                article.setTitle(weibo.get("title"));
                article.setLink(PAGE_URL.replace("{weibo_id}", id).replace("{user_id}", userId));
                Set<String> clientIds = WeiBoStore.findCidByUserId(userId);
                for (String cid : clientIds) {
                    AlertMeClient.pushByCid(cid)
                            .setArticle(article)
                            .send();
                    RedisHelper.addPushSet(WeiBoResource.Tag, id);
                }
            }

        } catch (HttpRequestException e) {
            e.printStackTrace();
        }


    }

    private List<Map<String, String>> formatResponse(String response) {
        JsonObject json = JsonUtil.format(response, JsonObject.class);
        JsonArray weiboList = json.getAsJsonArray("statuses");
        List<Map<String, String>> result = new ArrayList<>();
        for (int i=0; i<weiboList.size(); i++) {
            JsonObject weibo = weiboList.get(i).getAsJsonObject();
            // 是否是转发微博
            boolean isReTweeted = weibo.get("retweeted_status") != null;
            if(!isReTweeted) {
                String content = weibo.get("text").getAsString();
                content = weiboContentProcess(content);
                result.add(CollectionUtil.map()
                        .put("id", weibo.get("id").getAsString())
                        .put("title", content)
                        .put("uid", weibo.get("user").getAsJsonObject().get("idstr").getAsString())
                        .build());
            }
        }

        return result;
    }

    private String weiboContentProcess(String content) {
        // replace url
        String urlRegex = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        Pattern urlPattern = Pattern.compile(urlRegex);
        Matcher urlMatcher = urlPattern.matcher(content);
        List<String> urls = new ArrayList<>();
        while (urlMatcher.find()) {
            urls.add(content.substring(urlMatcher.start(), urlMatcher.end()));
        }

        for (String url : urls) {
            content = content.replaceAll(url, "[Link]");
        }

        return content;
    }

}
