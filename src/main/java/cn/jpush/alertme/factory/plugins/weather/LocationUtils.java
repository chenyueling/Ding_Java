package cn.jpush.alertme.factory.plugins.weather;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.JsonUtil;
import com.google.gson.JsonObject;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用百度API 输入一个坐标对,返回城市
 * Created by chenyueling on 2015/2/6.
 */
public class LocationUtils {

    public static final String AK = "F1cd9e2f525519fda33d2b1cb221d0e4";

    public static final String api = "http://api.map.baidu.com/geocoder/v2/?ak={ak}&location={lat},{lng}&output=json";

    public static String locationTranslateCity(String lat,String lng) throws HttpRequestException {
        String aip = api.replace("{ak}",AK).replace("{lat}",lat).replace("{lng}",lng);
        String json = NativeHttpClient.get(aip);
        JsonObject jsonObject = JsonUtil.format(json, JsonObject.class);
        return jsonObject.get("result").getAsJsonObject().get("addressComponent").getAsJsonObject().get("city").getAsString();


    }

    public static void main(String[] args) throws HttpRequestException {
        List<Map<String,String>> mapList = new ArrayList<>();

        Map<String ,String> map = new HashMap<>();
        map.put("lat","23.128925");
        map.put("lng","114.377117");
        mapList.add(map);

        map = new HashMap<>();
        map.put("lat","22.275418");
        map.put("lng","113.528541");
        mapList.add(map);


        map = new HashMap<>();
        map.put("lat","29.316214");
        map.put("lng","113.158296");
        mapList.add(map);

        map = new HashMap<>();
        map.put("lat","23.744314");
        map.put("lng","114.699069");
        mapList.add(map);

        map = new HashMap<>();
        map.put("lat","23.544314");
        map.put("lng","113.259481");
        mapList.add(map);


        map = new HashMap<>();
        map.put("lat","28.006825");
        map.put("lng","106.742238");
        mapList.add(map);

        for (Map<String, String> data : mapList) {
            System.out.println(locationTranslateCity(data.get("lat"), data.get("lng")));
            WeatherUtils.Result result = WeatherUtils.getWeatherByCity(locationTranslateCity(data.get("lat"), data.get("lng")));
            String weather = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_TODAY).get(WeatherUtils.WEATHER_DATA_WEATHER);
            String temperature = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_TODAY).get(WeatherUtils.WEATHER_DATA_TEMPERATURE);
            System.out.println(weather);
            System.out.println(temperature);
        }

    }


}
