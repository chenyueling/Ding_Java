package cn.jpush.alertme.factory.common;

import cn.jpush.alertme.factory.plugins.example.ExampleJob;
import cn.jpush.alertme.factory.util.StringUtil;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Quartz Common Method
 * Created by ZeFanXie on 14-12-17.
 */
public class QuartzHelper {
    private static Logger Log = LoggerFactory.getLogger(QuartzHelper.class);
    private static final String JOB_GROUP = "COMMON_GROUP";
    private static final String ONE_TIMES_GROUP = "ONE_TIMES_GROUP";
    private static Scheduler scheduler = null;

    public static void init() {
        QuartzHelper.getScheduler();
    }


    public static synchronized Scheduler getScheduler() {
        if (scheduler == null) {
            SchedulerFactory schedulerFact = new org.quartz.impl.StdSchedulerFactory();
            try {
                scheduler = schedulerFact.getScheduler();
                scheduler.start();
                Log.info("Quartz Running....");
            } catch (SchedulerException e) {
                Log.error(String.format("Quartz Start Fail : %s", e.getMessage()));
                e.printStackTrace();
            }
        }
        return scheduler;
    }
    public static void removeJob(String name, String group) throws SchedulerException {
    	if(StringUtils.isEmpty(name)||StringUtils.isEmpty(group)){
    		return;
    	}
    	JobKey jobKey = new JobKey(name, group);
    	TriggerKey triggerKey = new TriggerKey(name, group);
    	Scheduler scheduler = getScheduler();
    	scheduler.pauseTrigger(triggerKey);
    	scheduler.unscheduleJob(triggerKey);
    	scheduler.deleteJob(jobKey);
    }
    public static void addOneTimesJob(String name, int laterMinute, Class<? extends Job> job) throws SchedulerException {
        JobDetail jobDetail= JobBuilder.newJob(job)
                .withIdentity(name + "-" + StringUtil.getRandomString(6), ONE_TIMES_GROUP)
                .build();
        Trigger trigger= TriggerBuilder.newTrigger()
                .withIdentity(name + "-" + StringUtil.getRandomString(6), ONE_TIMES_GROUP)
                .startAt(DateBuilder.futureDate(laterMinute, DateBuilder.IntervalUnit.MINUTE))
                .build();
        QuartzHelper.getScheduler().scheduleJob(jobDetail, trigger);
    }
    /**
     * 带jobData的添加job方法
     * jobData供clientService 使用
     * @param name
     * @param cron
     * @param job
     * @param jobData
     * @throws SchedulerException
     */
    public static void addSingleJob(String name, String cron, Class<? extends Job> job, Map<String, ? extends Object> jobData, boolean isStartNow) throws SchedulerException {
    	QuartzHelper.addSingleJob(name, JOB_GROUP,  cron, job, jobData, isStartNow);
    }
    public static void addSingleJob(String name, String cron, Class<? extends Job> job, boolean isStartNow) throws SchedulerException{
    	QuartzHelper.addSingleJob(name,JOB_GROUP, cron, job, null, isStartNow);
    }
    public static void addSingleJob(String name, String group, String cron, Class<? extends Job> job, Map<String, ? extends Object> jobData, boolean isStartNow) throws SchedulerException {
    	Scheduler scheduler = QuartzHelper.getScheduler();
        JobKey jobKey = new JobKey(name, group);
        JobDetail _job = scheduler.getJobDetail(jobKey);
        if (_job == null) {
            QuartzHelper.addJob(name, group, cron, job, jobData, isStartNow);
        }
    }
    /**
     * 添加单例任务(如果name存在的话,则不会创建)
     * @param name
     * @param cron
     * @param job
     * @throws SchedulerException
     */
    public static void addSingleJob(String name, String cron, Class<? extends Job> job) throws SchedulerException {
        QuartzHelper.addSingleJob(name, JOB_GROUP, cron, job);
    }

    /**
     * 添加单例任务(如果name存在的话,则不会创建)
     * @param name
     * @param cron
     * @param job
     * @throws SchedulerException
     */
    public static void addSingleJob(String name, String group, String cron, Class<? extends Job> job) throws SchedulerException {
        Scheduler scheduler = QuartzHelper.getScheduler();
        JobKey jobKey = new JobKey(name, group);
        JobDetail _job = scheduler.getJobDetail(jobKey);
        if (_job == null) {
            QuartzHelper.addJob(name, group, cron, job);
        }
    }

    /**
     * 添加任务
     * @param name
     * @param cron
     * @param job
     * @throws SchedulerException
     */
    public static void addJob(String name, String cron, Class<? extends Job> job) throws SchedulerException {
        QuartzHelper.addJob(name, JOB_GROUP, cron, job);
    }
    
    /**
     * 添加任务
     * @param name
     * @param cron
     * @param job
     * @throws SchedulerException
     */
    public static void addJob(String name, String group, String cron, Class<? extends Job> job) throws SchedulerException {
        JobDetail jobDetail= JobBuilder.newJob(job)
                .withIdentity(name, group)
                .build();
        Trigger trigger= TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
        QuartzHelper.getScheduler().scheduleJob(jobDetail, trigger);
    }

    public static void addJob(String name, String group, String cron, Class<? extends Job> job, Map<String,? extends Object> jobData, boolean isStartNow) throws SchedulerException{
    	JobBuilder jobBuilder = JobBuilder.newJob(job)
    			.withIdentity(name, group);
    	if(jobData!=null && !jobData.isEmpty()){
    		for(Entry<String, ? extends Object> entry: jobData.entrySet()){
    			String key = entry.getKey();
    			if(!StringUtils.isEmpty(key)){
    				Object value = entry.getValue();
    				if(value instanceof Boolean){
    					jobBuilder.usingJobData(key, (Boolean)value);
    				} else if (value instanceof Double) {
    					jobBuilder.usingJobData(key, (Double)value);
    				} else if (value instanceof Float) {
    					jobBuilder.usingJobData(key, (Float)value);
    				} else if (value instanceof Integer) {
    					jobBuilder.usingJobData(key, (Integer)value);
    				} else if(value instanceof Long) {
    					jobBuilder.usingJobData(key, (Long)value);
    				} else if (value instanceof String) {
    					jobBuilder.usingJobData(key, (String)value);
    				} else {
    					//pass invalid value type
    				}
    			}
    		}
    	}
    	JobDetail jobDetail = jobBuilder.build();
    	TriggerBuilder triggerBuilder= TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron));
    	if(isStartNow){
    		triggerBuilder.startNow();
    	}
        Trigger trigger =triggerBuilder.build();
    	QuartzHelper.getScheduler().scheduleJob(jobDetail, trigger);
    }

    private QuartzHelper() {}
    
    /**
     * 重新设置定时任务，并且执行
     */
    public static void resetCron(String name, String group, String newCron, boolean startNow) throws SchedulerException{
    	if(StringUtils.isEmpty(name)||StringUtils.isEmpty(group)||StringUtils.isEmpty(newCron)){
    		return ;
    	}
    	Scheduler scheduler = QuartzHelper.getScheduler();
    	JobKey jobKey = new JobKey(name, group);
    	JobDetail jobDetail = scheduler.getJobDetail(jobKey);
    	List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
    	if(triggers!=null && !triggers.isEmpty()) {
    		//自身业务中一个jobDetail 只对应一个CronTrigger 并且jobName=triggerName  jobGroup=triggerGroup
    		//只有一个
    		CronTrigger cronTrigger = (CronTrigger)triggers.get(0);
    		String oldCron = cronTrigger.getCronExpression();
    		TriggerKey triggerKey = cronTrigger.getKey();
    		if(!newCron.equalsIgnoreCase(oldCron)){
//    			scheduler.pauseTrigger(triggerKey);//暂停老的trigger
    			TriggerBuilder<CronTrigger> triggerBuilder= TriggerBuilder.newTrigger()
    	                .withIdentity(name, group)
    	                .withSchedule(CronScheduleBuilder.cronSchedule(newCron))
    	                .forJob(jobDetail);
    			if(startNow){
    				triggerBuilder.startNow();
    			}
    			Trigger newTrigger = triggerBuilder.build();
    			scheduler.rescheduleJob(triggerKey, newTrigger);
    		}
    	}
    }
    public static void main(String[] args) throws SchedulerException {
        //QuartzHelper.addSingleJob("TestJob", "0 */1 * * * ?", ExampleJob.class);
        QuartzHelper.addOneTimesJob("TestJob", 1, ExampleJob.class);
    }
}
