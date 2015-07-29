package cn.jpush.alertme.factory.plugins.chaweizhang;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 刷新carHead<——>cityId的映射
 * @author Javen
 *
 */
public class CityConfigsCacheReloadJob implements Job {
	private static final Logger Log = LoggerFactory.getLogger(CityConfigsCacheReloadJob.class);
	@Override
	public void execute(JobExecutionContext context)
	        throws JobExecutionException {
		// TODO Auto-generated method stub
			try {
	            CityConfigsCache.reload();
            } catch (HttpRequestException e) {
	            // TODO Auto-generated catch block
	            if(Log.isErrorEnabled()){
	            	Log.error("[Reload CityConfigs Faild]"+e.getMessage());
	            }
            }

	}

}
