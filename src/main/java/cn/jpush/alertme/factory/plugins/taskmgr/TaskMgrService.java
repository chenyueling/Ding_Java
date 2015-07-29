package cn.jpush.alertme.factory.plugins.taskmgr;

import org.quartz.SchedulerException;

import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/27.
 */
public interface TaskMgrService {
    public List<Map<String, String>> getTasks() throws SchedulerException;

    public void updateTaskCron(String name,String group,String newCron,boolean starNow ) throws SchedulerException;

    public void deleteTask(String name,String group) throws SchedulerException;
}

