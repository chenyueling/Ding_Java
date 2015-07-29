package cn.jpush.alertme.factory.plugins.oli;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class OliConfigReloadJob implements Job{

	@Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
	    // TODO Auto-generated method stub
	    OliConfigCache.reload();
    }

}
