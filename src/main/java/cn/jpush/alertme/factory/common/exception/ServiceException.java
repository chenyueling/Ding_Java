package cn.jpush.alertme.factory.common.exception;

/**
 * 业务逻辑错误统一Exception
 * Created by ZeFanXie on 15-1-5.
 */
public class ServiceException extends Exception {
    public ServiceException(String message, Throwable e) {
        super(message, e);
    }
}
