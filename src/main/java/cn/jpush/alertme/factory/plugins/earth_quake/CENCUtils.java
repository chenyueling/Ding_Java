package cn.jpush.alertme.factory.plugins.earth_quake;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by chenyueling on 14-10-14.
 */
public class CENCUtils {
    private static final String HOST = "http://www.ceic.ac.cn/";
    public static final String detailLink = "http://news.ceic.ac.cn/{pageId}.html";
    /**
     * num 代表的数
     * <p/>
     * 1 最近24小时地震信息
     * 2 最近48小时地震信息
     * 3 最近7天地震信息
     * 4 最近30天地震信息
     * 5 最近一年3.0级以上地震信息
     * 6 最近一年地震信息
     * 7 最近一年3.0级以下地震
     * 8 最近一年4.0级以上地震信息
     * 9 最近一年5.0级以上地震信息
     * 0 最近一年6.0级以上地震信息
     */
    private static final String path = "ajax/speedsearch?num={type}&&page=1";

    public static String getData(String uri) throws MalformedURLException {

        String json = null;
        InputStream inputStream = null;
        try {
            json = NativeHttpClient.get(uri);
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Result getRecentEarthQuakeInfo(String type) {
        Result result = null;
        try {
            String url = HOST + path.replace("{type}", type);
            System.out.println(url);
            String json = getData(url).replace("(", "").replace(")", "");
            System.out.println(json);
            Gson gson = new Gson();
            System.out.println(gson);
            result = gson.fromJson(json, Result.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }


    public class Result {
        public String jieguo;
        public String page;
        public int num;
        public ArrayList<Item> shuju;

        public class Item {
            public String id;
            public String CATA_ID;
            public String SAVE_TIME;
            public String O_TIME;
            public String EPI_LAT;
            public String EPI_LON;
            public String EPI_DEPTH;
            public String AUTO_FLAG;
            public String EQ_TYPE;
            public String O_TIME_FRA;
            public String M;
            public String M_MS;
            public String M_MS7;
            public String M_ML;
            public String M_MB;
            public String M_MB2;
            public String SUM_STN;
            public String LOC_STN;
            public String LOCATION_C;
            public String LOCATION_S;
            public String CATA_TYPE;
            public String SYNC_TIME;
            public String IS_DEL;
            public String EQ_CATA_TYPE;
            public String NEW_DID;
        }
    }


    public static void main(String[] args) throws MalformedURLException {
        String url = HOST + path.replace("{type}", "0");
        String json = getData(url).replace("(", "").replace(")", "");
        System.out.println();
        Gson gson = new Gson();
        Result result = gson.fromJson(json, Result.class);

        System.out.println(result.jieguo);
        for (int i = 0; i < result.shuju.size(); i++) {
            System.out.println(result.shuju.get(i).LOCATION_C);
        }
    }


}
