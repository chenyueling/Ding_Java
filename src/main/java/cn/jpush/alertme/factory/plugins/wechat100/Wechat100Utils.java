package cn.jpush.alertme.factory.plugins.wechat100;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

/**
 * Created by chenyueling on 14-10-16.
 */
public class Wechat100Utils {

    private static String LINK = "http://www.100toutiao.com/index.php?g=Home&m=Index&a=show&cat={catid}&id={a_id}";

    private static String api = "http://www.100toutiao.com/index.php?g=Home&m=Hot&a=getHot24";

    private static String mobile_link = "http://www.100toutiao.com/index.php/Index/show/cat/{catid}/id/{a_id}.html";

    /**
     * 获取24小时最热新闻
     *
     * @return
     */
    public static List<Item> get24Hot() throws IOException, HttpRequestException {

        Gson gson = new Gson();
        String json = NativeHttpClient.get(Wechat100Utils.api);
        List<Item> items = gson.fromJson(json, new TypeToken<List<Item>>() {
        }.getType());

        return items;
    }


    public static void main(String[] args) throws IOException {
        List<Item> items = null;
        try {
            items = get24Hot();
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < items.size(); i++) {
            System.out.println(items.get(i).title);
            System.out.println(items.get(i).getLink());
        }
    }


    public class Item {

        public String a_id;
        public String likes;
        public String view;
        public String add_time;
        public String rank;
        public String title;
        public String title_msubstr;
        public String catid;

        public String getLink() {
            String link = mobile_link.replace("{catid}", this.catid).replace("{a_id}", this.a_id);
            return link;
        }
    }
}
