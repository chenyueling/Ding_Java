package cn.jpush.alertme.factory.plugins.toutiao;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenyueling on 2014/12/24.
 */
public class TouTiaoUtils {

    public static final String TITLE = "title";
    public static final String LINK = "link";

    private static final String HOST = "http://www.toutiao.com";

    private static final String NEWS_URL = HOST + "/toutiao/api/article/recent";
    private static final String GALLERY_URL = HOST + "/toutiao/api/gallery/recent";

    public static Map<String, String> getTutiaoNews() throws HttpRequestException {
        String json = NativeHttpClient.get(NEWS_URL);
        Map<String, String> map = formatNews(json);
        return map;
    }

    public static void main(String[] args) throws HttpRequestException {
        System.out.println(getTutiaoNews());
        System.out.println(getToutiaoGallerys());

    }

    public static Map<String, String> getToutiaoGallerys() throws HttpRequestException {
        String json = NativeHttpClient.get(GALLERY_URL);
        System.out.println(json);
        Map<String, String> map = formatGallery(json);
        return map;
    }

    private static Map<String, String> formatNews(String json) {
        JsonObject jsonObject = JsonUtil.format(json, JsonObject.class);
        JsonArray datas = jsonObject.getAsJsonArray("data");
        JsonObject data = datas.get(0).getAsJsonObject();
        Map<String, String> map = new HashMap<>();
        map.put(TouTiaoUtils.LINK, data.get("display_url").getAsString());
        map.put(TouTiaoUtils.TITLE, data.get("title").getAsString());
        return map;
    }

    private static Map<String, String> formatGallery(String json) {
        JsonObject jsonObject = JsonUtil.format(json, JsonObject.class);
        JsonArray datas = jsonObject.getAsJsonArray("data");
        for (JsonElement data : datas) {
            JsonObject obj = data.getAsJsonObject();

            if (obj.get("desc").getAsString() != null && "".endsWith(obj.get("desc").getAsString()) == false) {
                Map<String, String> map = new HashMap<>();
                map.put(TouTiaoUtils.LINK, HOST + obj.get("share_url").getAsString());
                map.put(TouTiaoUtils.TITLE, obj.get("desc").getAsString());
                return map;
            }
        }
        return null;
    }


}
