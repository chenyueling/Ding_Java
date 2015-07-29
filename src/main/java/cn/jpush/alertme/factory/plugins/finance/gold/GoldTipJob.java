package cn.jpush.alertme.factory.plugins.finance.gold;

import cn.jpush.alertme.factory.common.QuartzHelper;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.plugins.finance.FinanceTip;
import cn.jpush.alertme.factory.util.JsonUtil;
import com.google.gson.JsonObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by chenyueling on 2015/1/9.
 */
public class GoldTipJob extends FinanceTip implements Job {

    // private static final String KEY = "4d1e6aee5e5e4fab8075723511e267bf";
    /**
     * 当前接口每日50次请求次数
     * json 数据格式
     * <p/>
     * {
     * "error_code": 0,
     * "reason": "Success",
     * "result": [
     * {
     * "variety": "Ag(T+D)",
     * "latestpri": "3491.00",
     * "openpri": "3488.00",
     * "maxpri": "3520.00",
     * "minpri": "3486.00",
     * "limit": "-0.03%",
     * "yespri": "3492.00",
     * "totalvol": "1606426.00",
     * "time": "2015-01-09 11:30:39"
     * },
     * {
     * "variety": "Ag99.9",
     * "latestpri": "0.00",
     * "openpri": "0.00",
     * "maxpri": "0.00",
     * "minpri": "0.00",
     * "limit": "0.00%",
     * "yespri": "4092.00",
     * "totalvol": "0.00",
     * "time": "2015-01-08 19:07:28"
     * }
     * ]
     * }
     */
    // private static final String API = "http://apis.haoservice.com/lifeservice/gold/shgold?key={key}";
    private static final String API = "http://web.juhe.cn:8080/finance/gold/shgold?key={key}";

    //集合数据,这个keys 是 100次.可以使用三个月.
    private static final String KEY = "cf4c2761fcdb24437133d7174a4d5d06";

    /*
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Text text = new GoldTipJob().financeTip();
        try {
            AlertMeClient.pushByTag(getTag()).setText(text).send();
        } catch (HttpRequestException e) {
            try {
                QuartzHelper.addOneTimesJob(getTag(), 2, GoldTipJob.class);
            } catch (SchedulerException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, HttpRequestException {

        System.out.println(new GoldTipJob().getData());
    }

    public static String inputGzipStreamToString(InputStream in) throws IOException {
        GZIPInputStream gzipInputStream = new GZIPInputStream(in);
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = gzipInputStream.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }


   /* @Override //这里是聚合数据获取黄金开盘价的方法.
    public String getData() {
        String json = null;
        String uri = API.replace("{key}", GoldTipJob.KEY);
        try {
            json = NativeHttpClient.get(uri);
            JsonObject jsonObject = JsonUtil.format(json, JsonObject.class);

            if ("200".equals(jsonObject.get("resultcode").getAsString()) == false) {
                return null;
            }
                json = jsonElement.getAsJsonObject().get("2").getAsJsonObject().get("openpri").getAsString();
            }
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
        return json;
    }*/


    /**
     * 关于此方法的介绍,请戳
     * https://gist.githubusercontent.com/chenyueling/e3b975bc1d6706ba4a64/raw/8b2a304005ec9ee0a381c82e01efcd32b894793e/%E9%BB%84%E9%87%91%E6%8E%A5%E5%8F%A3.json
     *
     * @return
     */
    @Override
    public String getData() {
        URL url = null;
        try {
            url = new URL("http://api.jijinhao.com/realtime/quotejs.htm?categoryId=189&currentPage=1&pageSize=11&_=1422845378357");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            /**
             * Accept:
             * Accept-Encoding: gzip, deflate, sdch
             * Accept-Language: zh-CN,zh;q=0.8,en;q=0.6
             * User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36
             * Referer: http://www.cngold.org/img_date/shangjiaosuo.html
             * Connection: keep-alive
             * Host: api.jijinhao.com
             *
             */
            conn.setUseCaches(false);
            conn.addRequestProperty("Referer", "http://www.cngold.org/img_date/shangjiaosuo.html");
            String json = inputGzipStreamToString(conn.getInputStream());
            conn.disconnect();
            json = json.replace("var quot_str = [", "");
            json = json.substring(0, json.length() - 1);
            JsonObject jsonObj = JsonUtil.format(json, JsonObject.class);
            JsonObject AUTD = jsonObj.getAsJsonArray("data").get(0).getAsJsonObject().get("quote").getAsJsonObject();
            return AUTD.get("q1").getAsString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //当前服务没有阀值
    @Override
    public String getThreshold() {
        return "0";
    }

    @Override
    public Text buildPush(float dataF, float threasholdF) {
        Text text = new Text();
        text.setContent("今日黄金价格为 " + dataF + "元/克");
        return text;
    }

    @Override
    public String getTag() {
        return GoldTipResource.Tag;
    }
}
