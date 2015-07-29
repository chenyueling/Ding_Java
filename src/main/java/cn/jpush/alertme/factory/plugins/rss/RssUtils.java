package cn.jpush.alertme.factory.plugins.rss;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by chenyueling on 2015/1/27.
 */
public class RssUtils {


    public static SyndFeed RssXmlReader(String uri) throws IOException, FeedException {

        URL feedurl = new URL(uri); //指定rss位置
        URLConnection uc = feedurl.openConnection();
        //设置代理
        uc.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
        SyndFeedInput input = new SyndFeedInput();

        XmlReader xmlReader = new XmlReader(uc);
        SyndFeed feed = input.build(xmlReader);
        xmlReader.close();
        return feed;
    }


    public static SyndEntry RssFirstArticle(String uri) throws IOException, FeedException {

        URL feedurl = new URL(uri); //指定rss位置

        URLConnection uc = feedurl.openConnection();
        //设置代理
        uc.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");

        SyndFeedInput input = new SyndFeedInput();

        XmlReader xmlReader = new XmlReader(uc);
        SyndFeed feed = input.build(xmlReader);
        xmlReader.close();
        List<SyndEntry> list = feed.getEntries();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
