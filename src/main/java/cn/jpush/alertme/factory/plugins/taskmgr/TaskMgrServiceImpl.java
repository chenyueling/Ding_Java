package cn.jpush.alertme.factory.plugins.taskmgr;

import cn.jpush.alertme.factory.common.QuartzHelper;
import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.AbstractTrigger;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenyueling on 2015/1/27.
 */
@Component
public class TaskMgrServiceImpl implements TaskMgrService {

    public static final String TRIGGER_NAME = "triggerName";
    public static final String TRIGGER_GROUP_NAME = "triggerGroupName";
    public static final String TRIGGER_CRON_EXPRESSION = "triggerCronExpression";
    public static final String JOB_NAME = "jobName";
    public static final String JOB_CLASS = "jobClass";
    public static final String NEXT_FIRE_TIME = "nextFireTime";
    public static final String JOB_GROUP_NAME = "jobGroupName";


    @Override
    public List<Map<String, String>> getTasks() throws SchedulerException {

        Scheduler scheduler = QuartzHelper.getScheduler();
        List<String> list = scheduler.getJobGroupNames();
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.<JobKey>anyGroup());

        List<Map<String, String>> tasks = new ArrayList<Map<String, String>>();

        scheduler.getTriggerGroupNames();
        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
        for (TriggerKey triggerKey : triggerKeys) {
            Map<String, String> task = new HashMap<>();
            CronTrigger trigger;
            //这里过滤所有非cron任务.
            try {
                trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            } catch (ClassCastException e) {
                e.printStackTrace();
                continue;
            }
            JobKey jobKey = trigger.getJobKey();
            JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            task.put(TaskMgrServiceImpl.TRIGGER_NAME, triggerKey.getName());
            task.put(TaskMgrServiceImpl.TRIGGER_GROUP_NAME, ((AbstractTrigger) scheduler.getTrigger(triggerKey)).getGroup());
            task.put(TaskMgrServiceImpl.TRIGGER_CRON_EXPRESSION, trigger.getCronExpression());
            task.put(TaskMgrServiceImpl.JOB_NAME, jobKey.getName());
            task.put(TaskMgrServiceImpl.JOB_CLASS, jobDetail.getJobClass().getName());
            task.put(TaskMgrServiceImpl.JOB_GROUP_NAME, jobDetail.getGroup());
            task.put(TaskMgrServiceImpl.NEXT_FIRE_TIME, sdf.format(trigger.getNextFireTime()));
            tasks.add(task);
        }

        return tasks;
    }

    @Override
    public void updateTaskCron(String name, String group, String newCron, boolean starNow) throws SchedulerException {
        QuartzHelper.resetCron(name, group, newCron, starNow);
    }


    @Override
    public void deleteTask(String name, String group) throws SchedulerException {
        QuartzHelper.removeJob(name, group);

    }

    public static void main(String[] args) {
        try {
            System.out.println(new TaskMgrServiceImpl().getTasks());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


}
