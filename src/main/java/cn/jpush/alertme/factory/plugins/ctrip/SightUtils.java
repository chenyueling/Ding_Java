package cn.jpush.alertme.factory.plugins.ctrip;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chenyueling on 2014/11/29.
 */
public class SightUtils {


    private SightUtils() {

    }

    public static final String DISTRICTID = "{DistrictId}";
    public static final String SIGHTID = "{SightId}";
    public static final String RANK = "{Rank}";
    public static final String CITY_NAME = "{cityName}";
    public static final String index = "http://m.ctrip.com/restapi/you/DistrictAggApi/GetSightList?DistrictId={DistrictId}&Start=1&Count=10000&OrderType=1";
    public static final String sightPoint = "http://m.ctrip.com/you/detail/{DistrictId}/{SightId}/sight?districtName={cityName}&rank={Rank}";


    /**
     *
     *  2014-12-1
     * {
     *   "Result": [
     *    {
     *    "SightId": 2778,
     *    "Name": "世界之窗",
     *    "DistrictId": 26,
     *    "DistrictName": "深圳",
     *    "Rank": 1,
     *    "TicketPrice": 50,
     *    "TicketMarketPrice": 60,
     *    "ReferencePrice": 180,
     *    "Rating": 4.4,
     *    "CoverImageUrl": "http://dimg02.c-ctrip.com/images/tg/373/719/341/0fa43aa721984a9482ebcd3d2c4390bd_C_200_200.jpg",
     *    "CommentCount": 2864,
     *    "CurrentDistrictSightCount": 0,
     *    "Distance": -1,
     *    "Glat": 22.536157608032227,
     *    "Glon": 113.97076416015625
     *   }
     *   ],
     *   "DistrictName": "深圳",
     *   "TotalCount": 131,
     *   "Pinyin": null,
     *   "state": 0,
     *   "msg": null
     *   }
     *
     * @param districtId
     */


    public static String getJson(String districtId) throws HttpRequestException {
        String uri = SightUtils.index.replace("{DistrictId}", districtId);
        String json = NativeHttpClient.get(uri);
        return json;
    }

    public static void  updateJson(String districtId) throws HttpRequestException {
        String json = getJson(districtId);
        Post.setDistrictJsonCache(districtId,json);
    }


    /**
     * 城市ID
     *
     * @param districtId
     * @return
     */
    public static Sights getSights(String districtId) throws IOException, HttpRequestException {

        String json = null;
        json = Post.getDistrictJsonCache(districtId);
        if(json == null){
            String uri = SightUtils.index.replace("{DistrictId}", districtId);
            json = NativeHttpClient.get(uri);
            Post.setDistrictJsonCache(districtId,json);
        }
       // System.out.println(json);
        Gson gson = new Gson();
        Sights sights = gson.fromJson(json, Sights.class);
        return sights;
    }

    public static void main(String[] args) {
        try {
            System.out.println(getSights("26"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
    }

    public class Sights {
        public String DistrictName;
        public String TotalCount;
        public String Pinyin;
        public String state;
        public String msg;
        public ArrayList<Sight> Result;

        public class Sight {
            public String SightId;
            public String Name;
            public String DistrictId;
            public String DistrictName;
            public int Rank;
            public float TicketPrice;
            public float TicketMarketPrice;
            public float ReferencePrice;
            public float Rating;
            public String CoverImageUrl;
            public int CommentCount;
            public String CurrentDistrictSightCount;
            public float Distance;
            public float Glat;
            public float Glon;
        }
    }
}
