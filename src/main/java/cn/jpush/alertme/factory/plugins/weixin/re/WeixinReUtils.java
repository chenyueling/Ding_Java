package cn.jpush.alertme.factory.plugins.weixin.re;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
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
 * Created by chenyueling on 14-10-23.
 */
public class WeixinReUtils {


    public static final String TITLE = "title";
    public static final String LINK = "link";

    /**
     * 0 全部
     * 1 创意·科技
     * 2 媒体·达人
     * 3 娱乐·休闲
     * 4 生活·旅行
     * 5 学习·工具
     * 6 历史·读书
     * 7 金融·理财
     * 8 美食·菜谱
     * 9 电影·音乐
     * 10 汽车
     */
    public static final String[] types = {"all", "ideatech", "newsmedia", "fun", "lifejourney", "utility", "hisbook", "finance", "food", "moviemusic", "auto"};


    private static final String HOST = "http://www.weixin.re";


    public static List<Map<String, String>> analysis(int type) throws IOException {

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            String html = NativeHttpClient.get(HOST);
            Document document = Jsoup.parse(html);
            Element element = document.getElementById(types[type]).nextElementSibling().getElementsByTag("tbody").get(0);
            Elements elements = element.getElementsByTag("tr");

            for (int i = 0; i < elements.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                element = elements.get(i).getElementsByTag("a").get(1);
                map.put(WeixinReUtils.TITLE, element.html());
                map.put(WeixinReUtils.LINK, element.attr("href"));
                list.add(map);
            }
            System.out.println(list);
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static void main(String[] args) throws IOException {
        WeixinReUtils.analysis(2);
    }

}
