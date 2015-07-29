package cn.jpush.alertme.factory.plugins.chaweizhang;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 查违章需要根据车牌号前两位（eg. 粤B）来获取城市id
 * @author Javen
 *
 */
public class CityConfigsCache {
	private static final String RELOAD_URL = "http://www.cheshouye.com/api/weizhang/get_all_config";
	public static Map<String, Integer> cityCache = new HashMap<String, Integer>(); //read only cache
	public static Integer getCityId(String carHead) {
		if(cityCache!=null && !cityCache.isEmpty()) {
			return cityCache.get(carHead);
		}
		return null;
	}
	
	public static void initIfNeed() {
		if(cityCache.isEmpty()) {
			synchronized (CityConfigsCache.cityCache) {
	            if(cityCache.isEmpty()){
	            	try {
	                    reload();
                    } catch (HttpRequestException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
                    }
	            }
            }
		}
	}
	public static boolean init(){
		try {
	        reload();
        } catch (HttpRequestException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return false;
        }
		return true;
	}
	/**
	 * 加载缓存
	 * @throws HttpRequestException 
	 */
	public static void reload() throws HttpRequestException{
			Map<String, Integer> newCityCache = new HashMap<String, Integer>();
	        String response = NativeHttpClient.get(RELOAD_URL);
	        JsonObject jsonObject = JsonUtil.format(response, JsonObject.class);
	        JsonArray provinces = jsonObject.getAsJsonArray("configs");
	        if(provinces!= null &&provinces.size()>0){
	        	for(int i = 0;i<provinces.size();i++){
	        		JsonObject province = provinces.get(i).getAsJsonObject();
	        		if(province!=null && !province.isJsonNull()) {
	        			JsonArray citys = province.getAsJsonArray("citys");
	        			if(citys!=null&&citys.size()>0){
	        				for(int j = 0; j < citys.size(); j++){
	        					JsonObject city = citys.get(j).getAsJsonObject();
	        					if(city!=null && !city.isJsonNull()) {
	        						String carHead = city.get("car_head").getAsString();
//	        						String cityName = city.get("city_name").getAsString();
	        						Integer cityId = city.get("city_id").getAsInt();
	        						if(!StringUtils.isEmpty(carHead) && cityId!=null && cityId>=0/* && !StringUtils.isEmpty(cityName)*/){
	        							newCityCache.put(carHead, cityId);
	        						}
	        					}
	        				}
	        			}
	        		}
	        	}
	        }
	        if(!newCityCache.isEmpty()) {
	        	synchronized (CityConfigsCache.class) {
	        		cityCache.clear();
	        		cityCache = newCityCache;
                }
	        }
	}
}
