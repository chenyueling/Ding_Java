package cn.jpush.alertme.factory.plugins.knewone;

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
 * KnewOne Spider Job
 * Created by ZeFanXie on 14-12-23.
 */
public class SpiderJob implements Job {
    private static final Logger Log = LoggerFactory.getLogger(SpiderJob.class);
    private static final String EXPLORE_URL = "http://knewone.com/explore";
    private static final String POST_URL = "http://knewone.com/entries/{post_id}";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Log.debug("KnewOne Spider Execute....");

        try {
            String response = NativeHttpClient.get(EXPLORE_URL);

            Map<String, String> post = format(response);

            String id = post.get("id");
            if (!StringUtil.isEmpty(id)) {
                boolean isPushed = RedisHelper.inPushSet(KnewOneResource.Tag, id);
                if (isPushed) {
                    return;
                }

                //push
                Article article = new Article();
                article.setTitle(post.get("title"));
                article.setLink(POST_URL.replace("{post_id}", id));

                AlertMeClient.pushByTag(KnewOneResource.Tag).setArticle(article).send();
                RedisHelper.addPushSet(KnewOneResource.Tag,id);
            }

        } catch (HttpRequestException e) {
            e.printStackTrace();
        }

    }

    private Map<String, String> format(String response) {

        Document doc = Jsoup.parse(response);
        Elements articles = doc.getElementsByClass("title");
        String id = null;
        String title = null;
        if (articles != null && articles.size() > 0) {
            Element article = articles.get(0);
            Elements links = article.getElementsByTag("a");
            if (links != null && links.size() > 0) {
                Element link = links.get(0);
                String url = link.attr("href");
                String[] arr = url.split("/");
                title = link.html();
                if (arr.length == 3 && !StringUtil.isEmpty(title)) {
                    id = arr[2];
                }
            }
        }
        return CollectionUtil.map().put("id", id).put("title", title).build();
    }


}
