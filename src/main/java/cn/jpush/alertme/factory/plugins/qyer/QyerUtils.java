package cn.jpush.alertme.factory.plugins.qyer;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by chenyueling on 2014/12/2.
 */
public class QyerUtils {

    /**
     * 中间含有移动设备生成的
     * client_secret=9fcaae8aefc4f9ac4915
     */

    public static String Qyer_ID = "{id}";
    private static final String API = "http://open.qyer.com/lastminute/get_lastminute_list?client_id=qyer_android&client_secret=9fcaae8aefc4f9ac4915&v=1&track_deviceid=000000000000000&track_app_version=5.4.1&track_app_channel=qyer&track_device_info=vbox86p&track_os=Android4.4.2&track_user_id=&app_installtime=1417489486343&max_id=0&product_type=0&continent_id=0&country_id=0&departure=&times=&is_show_pay=1";
    public static final String detail = "http://m.qyer.com/z/deal/{id}/";
    public static void main(String[] args) throws URISyntaxException, IOException, HttpRequestException {
      getQyers();
    }



    public static Qyers getQyers() throws IOException, HttpRequestException {

        String json = NativeHttpClient.get(API);

        Gson gson = new Gson();

        Qyers qyers = gson.fromJson(json,Qyers.class);
        System.out.println(qyers.status);
        return qyers;
    }



    public class Qyers{
        public String status;
        public String info;
        public String times;
        public ArrayList<Qyer> data;



        public class Qyer{
            public String id;
            public String pic;
            public String title;
            public String detail;
            public String price;
            public long firstpay_end_time;
            public String end_date;
            public String booktype;
            public String self_use;
            public String buy_price;
            public String list_price;
            public String first_pub;
            public String perperty_lab_auth;
            public String perperty_today_new;
            public String lastminute_des;
            public String url;
        }
    }






}
