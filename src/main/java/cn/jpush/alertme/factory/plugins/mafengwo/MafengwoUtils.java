package cn.jpush.alertme.factory.plugins.mafengwo;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 14-10-20.
 */
public class MafengwoUtils {

    public static String HOST = "http://www.mafengwo.cn";

    public static final String api = "http://www.mafengwo.cn/ajax/ajax_article.php?start=1&type=0";

    public static final String LINK = "link";


    public static final String TITLE = "title";

    public static final String COVER_PIC = "cover_pic";

    public static final String SUMMARY = "summary";



    private MafengwoUtils() {

    }

    public static List<Map<String, String>> getHot() throws IOException, HttpRequestException {
        Gson gson = new Gson();
        String json = NativeHttpClient.get(api);
        Result result = gson.fromJson(json, Result.class);
        String html = result.html;
        List list = analysis(html);
        return list;
    }


    public static List<Map<String, String>> analysis(String html) {

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Document document = Jsoup.parse(html);

        Elements elements = document.getElementsByAttributeValue("class", "post_item");

        for (int i = 0; i < elements.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            Element element = elements.get(i);
            Element a1 = element.getElementsByTag("a").get(0);
            Element a2 = element.getElementsByTag("a").get(1);
            Element p3 = element.getElementsByTag("p").get(2);
            map.put(MafengwoUtils.COVER_PIC, a1.getElementsByTag("img").attr("src"));
            map.put(MafengwoUtils.LINK, MafengwoUtils.HOST + a1.attr("href"));
            map.put(MafengwoUtils.TITLE, a2.html());
            map.put(MafengwoUtils.SUMMARY,p3.html());
            list.add(map);
        }
        return list;
    }


    public class Result {
        public String ret;
        public String html;
        public String pagesize;
        public String recordcount;
        public String nowpage;
    }


    public static void main(String[] args) throws IOException, HttpRequestException {
        System.out.println(getHot());
    }


}
