package cn.jpush.alertme.factory.plugins.chaweizhang;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 查询查违章的接口，定时执行
 * @author Javen
 *
 */
public class RestInvokeJob implements Job{
	private static final Logger Log = LoggerFactory.getLogger(RestInvokeJob.class);
	private static Map<Integer, String> statusMap = new HashMap<Integer, String>();
	static {
		 	statusMap.put(1002,	"app_id有误");
			statusMap.put(1003,	"sign加密有误");
			statusMap.put(1004,	"车牌号，汽车类型，违章城市 等字段不能为空");
			statusMap.put(1005,	"carInfo有误");
			statusMap.put(2000,	"正常(无违章记录)");
			statusMap.put(2001,	"正常（有违章记录）");
			statusMap.put(5000,	"请求超时，请稍后重试");
			statusMap.put(5001,	"交管局系统连线忙碌中，请稍后再试");
			statusMap.put(5002,	"恭喜，当前城市交管局暂无您的违章记录");
			statusMap.put(5003,	"数据异常，请重新查询");
			statusMap.put(5004,	"系统错误，请稍后重试");
			statusMap.put(5005,	"车辆查询数量超过限制");
			statusMap.put(5006,	"你访问的速度过快, 请后再试");
			statusMap.put(5008,	"输入的车辆信息有误，请查证后重新输入");
	}
	private static final String TEXT_FORMAT = "[%s]%s";
	private static final String REST_URL = "http://www.cheshouye.com/api/weizhang/query_task?car_info=%s&sign=%s&timestamp=%s&app_id=%s";
	private static final String REST_APP_ID = "339";
	private static final String REST_APP_KEY = "d51983f2cc540bbe935754270901317c";
	@Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
	    JobDataMap data = context.getMergedJobDataMap();
	    String cityId = data.getString(ChaWeiZhangResource.PARAM_CITY_ID);
	    String hphm = data.getString(ChaWeiZhangResource.PARAM_HPHM);
	    String classno = data.getString(ChaWeiZhangResource.PARAM_CLASSNO);
	    String engineno = data.getString(ChaWeiZhangResource.PARAM_ENGINENO);
	    String carType = data.getString(ChaWeiZhangResource.PARAM_CAR_TYPE);
	    //before add this job we had vialdate those params. so we can use them directorily
	    JsonObject jsonObj = invoke(cityId, hphm, classno, engineno, carType);
	    Integer status = jsonObj.get("status").getAsInt();
	    if(status==2001){
	    	//有违章
	    	Integer count = jsonObj.get("count").getAsInt();
	    	if(count!=null && count>0){
	    		JsonArray historys = jsonObj.get("historys").getAsJsonArray();
	    		JsonObject history = historys.get(count).getAsJsonObject();
	    		if(history!=null){
	    			String occurDate = history.get("occur_date").getAsString();
	    			String info = history.get("info").getAsString();
	    			if(!StringUtils.isEmpty(occurDate) && ! StringUtils.isEmpty(info)) {
	    				try {
	                        AlertMeClient.pushByTag(ChaWeiZhangResource.Tag).setText(new Text(String.format(TEXT_FORMAT, occurDate, info))).send();
                        } catch (HttpRequestException e) {
	                        // TODO Auto-generated catch block
                        	if(Log.isErrorEnabled()) {
                        		Log.error("[Send Notfication Fail]" + e.getMessage());
                        	}
                        }
	    			}
	    		}
	    	}
	    }
	    
    }
	private static String getUrl(String cityId, String hphm, String classno, String engineno, String carType){
		String carInfo = "{hphm="+hphm+"&classno="+classno+"&engineno="+engineno+"&registno=&city_id="+cityId+"&car_type="+carType+"}";
		String timestamp = new Date().getTime()+"";
		String signTmp = REST_APP_ID + carInfo + timestamp + REST_APP_KEY;
		String sign = StringUtil.toMD5(signTmp);
		try {
	        carInfo = URLEncoder.encode(carInfo, "utf-8");
        } catch (UnsupportedEncodingException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		String url = String.format(REST_URL, carInfo, sign, timestamp, REST_APP_ID);
		return url;
	}
	private static JsonObject  invoke(String cityId, String hphm, String classno, String engineno, String carType) {
		String url = getUrl(cityId, hphm, classno, engineno, carType);
		try {
	        String response = NativeHttpClient.get(url);
	        JsonObject jsonObject = JsonUtil.format(response, JsonObject.class);
	        return jsonObject;
        } catch (HttpRequestException e) {
	        // TODO Auto-generated catch block
	       e.printStackTrace();
        }
		return null;
	}
	public static String checkParam(String cityId, String hphm, String classno, String engineno, String carType) {
		JsonObject jsonObj = invoke(cityId, hphm, classno, engineno, carType);
		Integer status = jsonObj.get("status").getAsInt();
		if(status==2000 || status == 2001) {
			return "Success";
		}else {
			return statusMap.get(status);
		}
	}
//	public static void main(String [] args){
//		String cityId="152";
//		String hphm="粤B12345";
//		String classno="123456";
//		String engineno="1234";
//		String carType="02";
//		JsonObject obj = invoke(cityId, hphm, classno, engineno, carType);
//		System.out.println(obj.toString());
//	}
}
