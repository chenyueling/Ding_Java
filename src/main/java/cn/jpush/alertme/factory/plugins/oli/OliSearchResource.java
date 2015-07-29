package cn.jpush.alertme.factory.plugins.oli;

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

@Path("/oli")
public class OliSearchResource extends BaseResource {
	private static final Logger Log = LoggerFactory
	        .getLogger(OliSearchResource.class);
	private static final String JOB_CRON_OLI_SEARCH = "0 0 8 * * ?"; //每天早上8点
	private static final String JOB_CRON_OLI_SEARCH_DEBUG = "0 */2 * * * ?"; //debug 下每两分钟一次
	private static final String JOB_NAME_RELOAD_OLI_CONFIGS_CACHE = "realod_oli_configs";
	private static final String JOB_CRON_RELOAD_OLI_CONFIGS_CACHE = "0 0 0 * * ?"; //每天凌晨0点
	public static final String Tag = "OliSearch";
	
	@Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid,
            String apiSecret, String title, Map<String, String> sData,
            List<CData> cData) {
	    if(OliConfigCache.init()){
	    	try {
	            QuartzHelper.addSingleJob(JOB_NAME_RELOAD_OLI_CONFIGS_CACHE,Tag, JOB_CRON_RELOAD_OLI_CONFIGS_CACHE, OliConfigReloadJob.class, null,false);
            } catch (SchedulerException e) {
	            // TODO Auto-generated catch block
	            //e.printStackTrace();
            	if(Log.isErrorEnabled()) {
            		Log.error(e.getMessage());
            	}
            }
	    	return super.onServiceCreate(resp, sid, apiSecret, title, sData, cData);
	    } else {
	    	HttpContextHelper.writeInvalidParameterResponse(resp, "Init City Configs Fail");
	    	return false;
	    }
    }
	
	
	@Override
    protected boolean onFollowedChange(HttpServletResponse resp, String sid,
            String cid, Long followed) {
	    if(followed<=0L){
	    	try {
	            QuartzHelper.removeJob(Tag+"_"+cid, Tag);
	            QuartzHelper.removeJob(Tag+"_"+cid+"_debug", Tag+"_debug");
//	            QuartzHelper.removeJob(JOB_NAME_RELOAD_OLI_CONFIGS_CACHE, Tag);
            } catch (SchedulerException e) {
	            // TODO Auto-generated catch block
	            if(Log.isErrorEnabled()){
	            	Log.error(e.getMessage());
	            }
            }
	    } else {
	    	ClientServiceDao clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
	    	if(clientServiceDao!=null){
	    		//每次有用户关注的时候都会进行一次db查询，可能是性能瓶颈
	    		ClientService clientService = clientServiceDao.findById(cid);
	    		if(clientService!=null){
	    			Map<String, String> data = clientService.getData();
	    			if(data!=null&&!data.isEmpty()){
	    				OliConfigCache.initIfNeed();
	    			    String city = data.get("city");
	    			    String oliType = data.get("oli_type");
	    			    boolean debug = Boolean.parseBoolean(data.get("debug"));
	    			    String cron = debug ? JOB_CRON_OLI_SEARCH_DEBUG : JOB_CRON_OLI_SEARCH;
	    			    String jobName = Tag+"_"+cid+(debug?"_debug":""); //用cid做job名后缀 (不用参数是因为在取消关注时的回调只传过来cid参数，没有其他参数信息)
	    			    String jobGroup = Tag+(debug?"_debug":"");
	    			    if(!StringUtils.isEmpty(city)&&!StringUtils.isEmpty(oliType)) {
	    			    	try {
	    			            QuartzHelper.addJob(jobName, jobGroup, cron, OliSearchJob.class, data, false);
	    			            return super.onClientServiceCreate(resp, sid, cid, data);
	    		            } catch (SchedulerException e) {
	    			            // TODO Auto-generated catch block
	    			            if(Log.isErrorEnabled()) {
	    			            	Log.error(e.getMessage());
	    			            }
	    			            HttpContextHelper.writeTipMessageResponse(resp, "内部错误");
	    			            return false;
	    		            }
	    			    } else {
	    			    	//查询不成功，则不创建
	    					HttpContextHelper.writeTipMessageResponse(resp, "非法的参数");
	    					return false;
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

}
