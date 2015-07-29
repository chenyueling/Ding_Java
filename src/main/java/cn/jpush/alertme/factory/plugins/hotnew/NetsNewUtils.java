package cn.jpush.alertme.factory.plugins.hotnew;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2014/9/24.
 */
public class NetsNewUtils {

    public static final String link = "http://j.news.163.com";
    public static final String mobileLink = "http://j.news.163.com/touch/#detail/99/{article_id}";
    public static final Logger LOG = LoggerFactory.getLogger(NetsNewUtils.class);
    public static final String TITLE ="title";
    public static final String LINK = "LINK";

    public static List<Map<String, String>> catchHotNew() throws IOException, HttpRequestException {
        LOG.info("[NetsNewUtils cathHotNew] begin");
        String html = NativeHttpClient.get(link);
        Document document = Jsoup.parse(html);
        Element hotRank = document.getElementsByAttributeValue("id", "ranking-wrapper").get(0);
        hotRank.getAllElements();
        //System.out.println();
        List<Node> list = hotRank.childNodes();

        List<Map<String, String>> resutl = new ArrayList<Map<String, String>>();

        for (int i = 0; i < list.size(); i++) {
            Document document1 = Jsoup.parse(list.get(i).toString());
            Elements obj = document1.getElementsByAttribute("title");
            Map<String, String> map = new HashMap<String, String>();
            String title = obj.attr("title");
            String href = obj.attr("href");
            if ((title != null && "".equals(title) == false) && (href != null && "".equals(href) == false)) {
                map.put("title", title);
                href = transferURlToMobileDevice(href);
                map.put("href", href);
                //LOG.info("[NetsNewUtils ]" + map);
                resutl.add(map);
            }
        }
        return resutl;
    }


    public static void main(String[] args) throws IOException, HttpRequestException {
        List<Map<String, String>> list = catchHotNew();

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).get("title") + "----" + list.get(i).get("href"));
        }
    }


    public static String transferURlToMobileDevice(String url) {
        String article_id = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".html"));
        String href = NetsNewUtils.mobileLink.replace("{article_id}", article_id);
        return href;
    }
}
