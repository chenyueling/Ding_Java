package cn.jpush.alertme.factory.plugins.chaweizhang;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.http.HttpContextHelper;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

@Path("/chaweizhang")
public class ChaWeiZhangResource extends BaseResource {
	private static final Logger Log = LoggerFactory
	        .getLogger(ChaWeiZhangResource.class);
	private static final String JOB_NAME_RELOAD_CITY_CONFIGS_CACHE = "realod_city_configs";
	private static final String JOB_CRON_RELOAD_CITY_CONFIGS_CACHE = "0 0 0 * * ?";
	private static final String JOB_CRON_CS_CREATE = "0 0 */2 * * ?"; //每两小时
	public static final String Tag = "ChaWeiZhang";
	//车牌号码
	public static String PARAM_HPHM = "hphm";
	//车架号
	public static String PARAM_CLASSNO = "classno";
	//发动机号
	public static String PARAM_ENGINENO = "engineno";
	
	//city id
	public static String PARAM_CITY_ID = "city_id";
	
	//车类型
	public static String PARAM_CAR_TYPE = "car_type";
	
	/**
	 * 第一次创建service的时候会触发这个方法
	 * 这里加载车牌头到城市id的映射
	 */
	@Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid,
            String apiSecret, String title, Map<String, String> sData,
            List<CData> cData) {
	    if(CityConfigsCache.init()) {
	    	//如果成功，添加定时任务 1天一次
	    	try {
	            QuartzHelper.addSingleJob(JOB_NAME_RELOAD_CITY_CONFIGS_CACHE, Tag, JOB_CRON_RELOAD_CITY_CONFIGS_CACHE, CityConfigsCacheReloadJob.class,null, false);
            } catch (SchedulerException e) {
	            // TODO Auto-generated catch block
            	if(Log.isErrorEnabled()){
            		Log.error("[Add SingleJob Fail]"+e.getMessage());
            	}
            }
	    	return super.onServiceCreate(resp, sid, apiSecret, title, sData, cData);
	    }else{
	    	HttpContextHelper.writeInvalidParameterResponse(resp, "Init City Configs Fail");
	    	return false;
	    }
    }

	/**
	 * 每次有user 关注service的时候都会触发onclientServiceCreate方法，这里就添加一个定时任务
	 */
	@Override
    protected boolean onClientServiceCreate(HttpServletResponse resp,
            String sid, String cid, Map<String, String> data) {
		CityConfigsCache.initIfNeed();
		String ret;
		if((ret=validate(data)).equals("Success")){
		    return super.onClientServiceCreate(resp, sid, cid, data);
		}
		else {
			//查询不成功，则不创建
			HttpContextHelper.writeTipMessageResponse(resp, ret);
			return false;
		}
    }
	
	@Override
    protected boolean onFollowedChange(HttpServletResponse resp, String sid,
            String cid, Long followed) {
		if(followed<=0L){
			//如果followed为0表示没人关注了，释放job
			try {
	            QuartzHelper.removeJob(Tag+"_"+cid, Tag);
//	            QuartzHelper.removeJob(JOB_NAME_RELOAD_CITY_CONFIGS_CACHE, Tag);
            } catch (SchedulerException e) {
	            // TODO Auto-generated catch block
	            if(Log.isErrorEnabled()){
	            	Log.error(e.getMessage());
	            }
            }
		} else {
			//>0添加任务
			String jobName = this.getTag()+"_"+cid;
			ClientServiceDao clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
			if(clientServiceDao!=null){
				ClientService clientService = clientServiceDao.findById(cid);
				if(clientService!=null){
					Map<String, String> data = clientService.getData();
					if(data!=null&&!data.isEmpty()){
						try {
					        QuartzHelper.addSingleJob(jobName,Tag,JOB_CRON_CS_CREATE, RestInvokeJob.class, data, false);
				        } catch (SchedulerException e) {
					        // TODO Auto-generated catch block
					        if(Log.isErrorEnabled()){
					        	Log.error("[Add Single Job Fail]"+e.getMessage());
					        }
				        }
					}
				}
			}
		}
	    return super.onFollowedChange(resp, sid, cid, followed);
    }

	@Override
	protected Logger getLog() {
		// TODO Auto-generated method stub
		return Log;
	}

	@Override
	protected String getTag() {
		// TODO Auto-generated method stub
		return Tag;
	}
	
	private String validate(Map<String, String> data) {
		String hphm = data.get(PARAM_HPHM);
		String classno = data.get(PARAM_CLASSNO);
		String engineno = data.get(PARAM_ENGINENO);
		String carType = data.get(PARAM_CAR_TYPE);
		if(!StringUtils.isEmpty(hphm) && hphm.length() > 2 && !StringUtils.isEmpty(classno) && !StringUtils.isEmpty(engineno)){
			if(StringUtils.isEmpty(carType)){
				data.put(PARAM_CAR_TYPE, "02");
			}
			String carHead = hphm.substring(0,2).toUpperCase();
			Integer cityId = CityConfigsCache.getCityId(carHead);
			if(cityId!=null && cityId>=0){
				data.put(PARAM_CITY_ID, cityId+"");
				return RestInvokeJob.checkParam(cityId+"", hphm, classno, engineno, carType);
			} 
		}
		return "Invalid Parameter";
	}
}
