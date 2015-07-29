package cn.jpush.alertme.factory.common;

/**
 * 推送失败接口
 * Created by chenyueling on 2014/12/23.
 */
public interface JobFailure {
    /**
     * 失败对象
     * @param jobFailure
     * @return
     */
    public boolean addToFailureQueue(JobFailure jobFailure);

    /**
     * 执行失败任务的定时器方法
     * @return
     */
    public boolean executeFailureJob();



}
