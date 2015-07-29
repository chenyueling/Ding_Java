package cn.jpush.alertme.factory.plugins.weather;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * 基于百度API http://api.map.baidu.com/telematics/v3/weather?location={city}&output=json&ak={ak}
 * ak = F1cd9e2f525519fda33d2b1cb221d0e4
 * Created by chenyueling on 2014/12/25.
 */
public class WeatherUtils {
    private static final String ak = "F1cd9e2f525519fda33d2b1cb221d0e4";
    private static final String api = "http://api.map.baidu.com/telematics/v3/weather?location={city}&output=json&ak={ak}";

    public static final String AK = "{ak}";

    public static final String CITY = "{city}";

    /**
     * 获取当前属性的标题
     */
    public static final String TITLE = "title";
    /**
     * 获取当前属性的指数
     */
    public static final String ZS = "zs";
    /**
     * 获取当前属性的指数
     */
    public static final String TIPT = "tipt";
    /**
     * 获取当前属性的描述
     */
    public static final String DES = "des";
    /**
     * 穿衣指数
     */
    public static final int INDEX_CYZS = 0;
    /**
     * 洗车指数
     */
    public static final int INDEX_XCZS = 1;
    /**
     * 旅游指数
     */
    public static final int INDEX_LYZS = 2;

    /**
     * 感冒指数
     */
    public static final int INDEX_GMZS = 3;
    /**
     * 运动指数
     */
    public static final int INDEX_YDZS = 4;
    /**
     * 紫外线指数
     */
    public static final int INDEX_XYXZS = 5;

    /**
     * 今天天气情况
     */
    public static final int WEATHER_DATA_TODAY = 0;
    /**
     * 明天天气情况
     */
    public static final int WEATHER_DATA_TOMORROW = 1;
    /**
     * 后天天气清楚
     */
    public static final int WEATHER_DATA_DAY_AFTER_TOMORROW = 2;
    /**
     * 大后天天气情况
     */
    public static final int WEATHER_DATA_THREE_DAY_FROM_NOW = 3;


    public static final String WEATHER_DATA_DAY = "date";
    public static final String WEATHER_DATA_DAY_PICTURE_URL = "dayPictureUrl";
    public static final String WEATHER_DATA_WEATHER = "weather";
    public static final String WEATHER_DATA_WIND = "wind";
    public static final String WEATHER_DATA_TEMPERATURE = "temperature";

    /**
     * 正确返回json
     * {
     * "error": 0,
     * "status": "success",
     * "date": "2014-12-25",
     * "results": [
     * {
     * "currentCity": "珠海",
     * "pm25": "174",
     * "index": [
     * {
     * "title": "穿衣",
     * "zs": "较舒适",
     * "tipt": "穿衣指数",
     * "des": "建议着薄外套、开衫牛仔衫裤等服装。年老体弱者应适当添加衣物，宜着夹克衫、薄毛衣等。"
     * },
     * {
     * "title": "洗车",
     * "zs": "不宜",
     * "tipt": "洗车指数",
     * "des": "不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"
     * },
     * {
     * "title": "旅游",
     * "zs": "适宜",
     * "tipt": "旅游指数",
     * "des": "温度适宜，又有较弱降水和微风作伴，会给您的旅行带来意想不到的景象，适宜旅游，可不要错过机会呦！"
     * },
     * {
     * "title": "感冒",
     * "zs": "易发",
     * "tipt": "感冒指数",
     * "des": "天冷空气湿度大，易发生感冒，请注意适当增加衣服，加强自我防护避免感冒。"
     * },
     * {
     * "title": "运动",
     * "zs": "较不宜",
     * "tipt": "运动指数",
     * "des": "有降水，推荐您在室内进行健身休闲运动；若坚持户外运动，须注意保暖并携带雨具。"
     * },
     * {
     * "title": "紫外线强度",
     * "zs": "最弱",
     * "tipt": "紫外线强度指数",
     * "des": "属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"
     * }
     * ],
     * "weather_data": [
     * {
     * "date": "周四 12月25日 (实时：19℃)",
     * "dayPictureUrl": "http://api.map.baidu.com/images/weather/day/xiaoyu.png",
     * "nightPictureUrl": "http://api.map.baidu.com/images/weather/night/xiaoyu.png",
     * "weather": "小雨",
     * "wind": "微风",
     * "temperature": "19 ~ 14℃"
     * },
     * {
     * "date": "周五",
     * "dayPictureUrl": "http://api.map.baidu.com/images/weather/day/xiaoyu.png",
     * "nightPictureUrl": "http://api.map.baidu.com/images/weather/night/xiaoyu.png",
     * "weather": "小雨",
     * "wind": "微风",
     * "temperature": "16 ~ 13℃"
     * },
     * {
     * "date": "周六",
     * "dayPictureUrl": "http://api.map.baidu.com/images/weather/day/xiaoyu.png",
     * "nightPictureUrl": "http://api.map.baidu.com/images/weather/night/duoyun.png",
     * "weather": "小雨转多云",
     * "wind": "微风",
     * "temperature": "17 ~ 13℃"
     * },
     * {
     * "date": "周日",
     * "dayPictureUrl": "http://api.map.baidu.com/images/weather/day/duoyun.png",
     * "nightPictureUrl": "http://api.map.baidu.com/images/weather/night/duoyun.png",
     * "weather": "多云",
     * "wind": "微风",
     * "temperature": "18 ~ 11℃"
     * }
     * ]
     * }
     * ]
     * }
     *
     *
     *
     * 错误返回json
     *
     * {
     * "error": -3,
     * "status": "No result available",
     * "date": "2014-12-25"
     * }
     **/


    public static Result getWeatherByCity(String city){
        if(city == null && "".equals(city) == true){
            return null;
        }
        String _api = WeatherUtils.api.replace(WeatherUtils.AK,WeatherUtils.ak).replace(WeatherUtils.CITY,city);

        Result result = null;

        try {
            URL url = new URL(_api);
            InputStream inputStream = url.openStream();
            String json = formatInputStream(inputStream);
            Gson gson = new Gson();
            result = gson.fromJson(json, Result.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

    public static void main(String[] args) {
        //多个城市使用 | 隔开
        getWeatherByCity("珠海|北京");
    }


    private static String formatInputStream(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public class Result{
        public String error;
        public String status;
        public String date;
        public ArrayList<CityWeather> results;

        public class CityWeather{
            public String currentCity;
            public String pm25;
            public ArrayList<Map<String ,String>> index;
            public ArrayList<Map<String ,String>> weather_data;
        }
    }
}
