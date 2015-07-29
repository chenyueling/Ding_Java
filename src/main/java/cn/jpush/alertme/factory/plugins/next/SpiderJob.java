package cn.jpush.alertme.factory.plugins.next;

import cn.jpush.alertme.factory.common.RedisHelper;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.CollectionUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Next Spider Job
 * Created by ZeFanXie on 14-12-24.
 */
public class SpiderJob implements Job{
    private static final Logger Log = LoggerFactory.getLogger(SpiderJob.class);
    private static final String INDEX_URL = "http://next.36kr.com/posts";
    private static final String PAGE_URL = "http://next.36kr.com/posts/{post_id}";


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Log.debug("Next Spider Execute....");

        try {
            String response = NativeHttpClient.get(INDEX_URL);
            Map<String, String> post = format(response);

            String id = post.get("id");
            if (!StringUtil.isEmpty(id)) {
                boolean isPushed = RedisHelper.inPushSet(NextResource.Tag, id);
                if (isPushed) {
                    return;
                }

                //push
                Article article = new Article();
                article.setTitle(post.get("title"));
                article.setSummary(post.get("summary"));
                article.setLink(PAGE_URL.replace("{post_id}", id));

                AlertMeClient.pushByTag(NextResource.Tag).setArticle(article).send();
                RedisHelper.inPushSet(NextResource.Tag, id);
            }
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }

    }

    private Map<String, String> format(String response) {
        String id = null;
        String title = null;
        String summary = null;

        try {
            Document doc = Jsoup.parse(response);
            Elements articles = doc.getElementsByClass("product-url");
            Element article = articles.get(0);
            id = article.getElementsByClass("post-url").get(0).attr("href").split("/")[2];
            title = article.getElementsByClass("post-url").get(0).html();
            summary = article.getElementsByClass("post-tagline").get(0).html();
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Spider Parsing Fail : " + e.getMessage());
        }
        return CollectionUtil.map().put("id", id).put("title", title).put("summary", summary).build();
    }

}
