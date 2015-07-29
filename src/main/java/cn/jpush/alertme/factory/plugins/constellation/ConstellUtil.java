package cn.jpush.alertme.factory.plugins.constellation;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * Created by chenyueling on 2015/1/14.
 */
public class ConstellUtil {

    private static final String KEY = "95602da882dc4e0cb7ff1279631a7030";
    private static final String API = "http://apis.haoservice.com/lifeservice/constellation/GetAll?consName={consName}&type=today&key={key}";

    public static Result.Fortune getConstellFortune(String constellName) {
        String uri = API.replace("{key}", KEY).replace("{consName}", constellName);


        String json = null;
        try {
            json = NativeHttpClient.get(uri);
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        ConstellUtil.Result result = gson.fromJson(json, ConstellUtil.Result.class);
        System.out.println(json);
        if ("0".equals(result.error_code)) {
            return result.result;
        }
        return null;
    }


    public static void main(String[] args) throws HttpRequestException, IOException {
        Result.Fortune fortune = getConstellFortune("摩羯座");
        System.out.println(fortune == null ? "" : fortune.toString());
        // getData();
    }


    /**
     * 网页抓取数据
     * @throws HttpRequestException
     * @throws IOException
     */
    private static void getData() throws HttpRequestException, IOException {
        //System.out.println(NativeHttpClient.get("http://www.d1xz.net/yunshi/today/Taurus/"));
        URL url = new URL("http://data.astro.qq.com/dayastro/75/75065/index.shtml");
        InputStream inputStream = url.openStream();
        Reader reader = new InputStreamReader(inputStream, "GBK");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer sb = new StringBuffer();
        while (bufferedReader.readLine() != null) {
            sb.append(bufferedReader.readLine()).append("\r\n");
        }
        String html = sb.toString();
        System.out.println(html);
        Document document = Jsoup.parse(html);

        System.out.println(document.getElementById("maintext"));
        System.out.println(document.getElementsByClass("span2"));


    }


    public class Result {
        public String error_code;
        public String reason;
        public Fortune result;

        public class Fortune {
            public String name;
            public String datetime;
            public String date;
            public String all;
            public String color;
            public String health;
            public String love;
            public String money;
            public String number;
            public String OFriend;
            public String summary;
            public String work;

            @Override
            public String toString() {
                StringBuffer sb = new StringBuffer();
                //sb.append(name).append("</br>").append("\n");
                sb.append("今日运势:").append("\t").append("</br>").append("\n");
                sb.append("幸运颜色:").append("\t").append(color).append("</br>").append("\n");
                sb.append("幸运数字:").append("\t").append(number).append("</br>").append("\n");
                sb.append("幸运星座:").append("\t").append(OFriend).append("</br>").append("\n");
                sb.append("健康运势:").append("\t").append(health).append("</br>").append("\n");
                sb.append("爱情运势:").append("\t").append(love).append("</br>").append("\n");
                sb.append("财运运势:").append("\t").append(money).append("</br>").append("\n");
                sb.append("工作运势:").append("\t").append(work).append("</br>").append("\n");
                sb.append("综合运势:").append("\t").append(all).append("</br>").append("\n");
                sb.append("整体运势:").append("\n").append(summary).append("</br>").append("\n");
                return sb.toString();
            }
        }
    }


}
