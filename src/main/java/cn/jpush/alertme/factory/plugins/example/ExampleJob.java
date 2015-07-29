package cn.jpush.alertme.factory.plugins.example;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * Created by ZeFanXie on 14-12-18.
 */
public class ExampleJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Example Job Running, now:" + new Date().toString());
    }
}
