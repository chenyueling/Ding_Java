package cn.jpush.alertme.factory.plugins.oli;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.util.StringUtils;

import java.util.Map;

public class OliSearchJob implements Job {

	@Override
	public void execute(JobExecutionContext context)
	        throws JobExecutionException {
		// TODO Auto-generated method stub
		Map<String, Object> jobData = context.getMergedJobDataMap();
		String city = (String)jobData.get("city");
		String oliType = (String)jobData.get("oli_type");
		String price = OliConfigCache.getPrice(city, oliType);
		if(!StringUtils.isEmpty(price)) {
			try {
	            AlertMeClient.pushByTag(OliSearchResource.Tag).setText(new Text(format(city, oliType, price))).send();
            } catch (HttpRequestException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		}
		
	}
	private String format(String city, String oliType, String price){
		if(price.indexOf(":")>0){
			return price;
		}
		StringBuilder sb = new StringBuilder();
		String fmtType = null;
		if("b90".equals(oliType)) {
			fmtType = "90号汽油";
		} else if ("b93".equals(oliType)) {
			fmtType = "93号汽油";
		} else if ("b97".equals(oliType)) {
			fmtType = "97号汽油";
		} else if ("b0".equals(oliType)) {
			fmtType = "柴油";
		} else {
			//pass
		}
		sb.append(city).append(fmtType).append(":").append(price);
		return sb.toString();
	}
}
