package cn.jpush.alertme.factory.plugins.movie_download;

import cn.jpush.alertme.factory.common.Config;
import cn.jpush.alertme.factory.common.RedisHelper;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.util.CollectionUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZeFanXie on 14-12-25.
 */
public class SpiderJob implements Job {
    private static final Logger Log = LoggerFactory.getLogger(SpiderJob.class);
    private static final String DY2018_URL = "http://www.dy2018.com";
    private static final String DYTT8_URL = "http://www.dytt8.net";
    private static Pattern TitlePattern = Pattern.compile("(?<=《)([.\\S\\s]*)(?=》)");


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Log.debug("Movie Download Spider Execute....");
        try {
            Map<String, String> movieDY2018 = movieSpiderForDY2018();
            if (movieDY2018 != null) {
                String id = new Date().getTime() + StringUtil.getRandomNum(4);
                // push
                Article article = new Article();
                article.setTitle(movieDY2018.get("title"));
                article.setLink(Config.ALERT_ME_NODE_HOST + "/movie/" + id);
                AlertMeClient.pushByTag(MovieDownloadResource.Tag).setArticle(article).send();

                new Movie(id,
                        movieDY2018.get("title"),
                        movieDY2018.get("fullTitle"),
                        movieDY2018.get("link"),
                        movieDY2018.get("content"))
                        .save();
                Log.debug(String.format("Movie %s Save Success", movieDY2018.get("title")));
                RedisHelper.addPushSet(MovieDownloadResource.Tag, movieDY2018.get("title"));
            }

            Map<String, String> movieDYTT8 = movieSpiderForDYTT8();
            if (movieDYTT8 != null) {
                String id = new Date().getTime() + StringUtil.getRandomNum(4);
                // push
                Article article = new Article();
                article.setTitle(movieDYTT8.get("title"));
                article.setLink(Config.ALERT_ME_NODE_HOST + "/movie/" + id);
                AlertMeClient.pushByTag(MovieDownloadResource.Tag).setArticle(article).send();

                new Movie(id,
                        movieDYTT8.get("title"),
                        movieDYTT8.get("fullTitle"),
                        movieDYTT8.get("link"),
                        movieDYTT8.get("content"))
                        .save();
                Log.debug(String.format("Movie %s Save Success", movieDYTT8.get("title")));

                RedisHelper.addPushSet(MovieDownloadResource.Tag, movieDYTT8.get("title"));
            }
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }

    }

    /**
     * 电影天堂 http://www.dy2018.com 爬虫
     * @return
     */
    private Map<String, String> movieSpiderForDY2018() {

        try {
            Document doc = Jsoup.connect(DY2018_URL).get();
            Element newMovieList = doc.getElementsByClass("co_content222").get(0);
            Element laterMovie = newMovieList.getElementsByTag("a").get(0);
            String link = laterMovie.attr("href");
            String fullTitle = laterMovie.html();
            String title;
            Matcher match = TitlePattern.matcher(fullTitle);
            if (match.find()) {
                title = match.group();
            } else {
                return null;
            }


            boolean inPushSet = RedisHelper.inPushSet(MovieDownloadResource.Tag, title);
            if (inPushSet) {
                return null;
            } else {
                Document post = Jsoup.connect(DY2018_URL + link).get();
                String content = post.getElementById("Zoom").html();

                if (StringUtil.isEmpty(title) || StringUtil.isEmpty(link) || StringUtil.isEmpty(content)) {
                    return null;
                }

                return CollectionUtil.map()
                        .put("title", title)
                        .put("fullTitle", fullTitle)
                        .put("link", link)
                        .put("content", content).build();

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 电影天堂 http://www.dytt8.net 爬虫
     * @return
     */
    private Map<String, String> movieSpiderForDYTT8() {
        try {
            Document doc = Jsoup.connect(DYTT8_URL).get();
            Element newMovieList = doc.getElementsByClass("co_content8").get(0);
            Element laterMovie = newMovieList.getElementsByTag("a").get(1);
            String link = DYTT8_URL + laterMovie.attr("href");
            String fullTitle = laterMovie.html();
            String title;
            Matcher match = TitlePattern.matcher(fullTitle);
            if (match.find()) {
                title = match.group();
            } else {
                return null;
            }


            boolean inPushSet =  RedisHelper.inPushSet(MovieDownloadResource.Tag, title);
            if (inPushSet) {
                return null;
            } else {
                Document post = Jsoup.connect(link).get();
                String content = post.getElementById("Zoom").html();

                if (StringUtil.isEmpty(title) || StringUtil.isEmpty(link) || StringUtil.isEmpty(content)) {
                    return null;
                }

                return CollectionUtil.map()
                        .put("title", title)
                        .put("fullTitle", fullTitle)
                        .put("link", link)
                        .put("content", content).build();

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) throws JobExecutionException {
        new SpiderJob().execute(null);
    }



}
