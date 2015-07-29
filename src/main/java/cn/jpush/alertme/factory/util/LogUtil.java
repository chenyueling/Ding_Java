package cn.jpush.alertme.factory.util;

import cn.jpush.alertme.factory.common.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * Created by ZeFanXie on 15-1-26.
 */
public class LogUtil {
    private static final String MAIN_PACKAGE = "cn.jpush.alertme.factory";

    private static String buildTag() {
        StackTraceElement currentStackTrace = Thread.currentThread().getStackTrace()[3];
        String className = currentStackTrace.getClassName();
        String method = currentStackTrace.getMethodName();
        int lineNumber = currentStackTrace.getLineNumber();
        return (className + "." + method).replace(MAIN_PACKAGE, "") + ":" + lineNumber;
    }

    private static boolean isDebug() {
        return Config.IS_DEBUG;
    }

    public static void d(String message) {
        if (isDebug()) {
            Logger log = LoggerFactory.getLogger(buildTag());
            log.debug(message);
        }
    }
    public static void i(String message) {
        if (isDebug()) {
            Logger log = LoggerFactory.getLogger(buildTag());
            log.info(message);
        }
    }
    public static void w(String message) {
        if (isDebug()) {
            Logger log = LoggerFactory.getLogger(buildTag());
            log.warn(message);
        }

    }
    public static void e(String message) {
        if (isDebug()) {
            Logger log = LoggerFactory.getLogger(buildTag());
            log.warn(message);
        }
    }

    public static void e(String message, Throwable e) {
        if (isDebug()) {
            StackTraceElement stackTrace = e.getStackTrace()[0];
            message = message + ":" + stackTrace.getClassName() + "." + stackTrace.getMethodName() + ":" + stackTrace.getLineNumber() + " " + e.getMessage();
            Logger log = LoggerFactory.getLogger(buildTag());
            log.warn(message);
        }
    }

    public static void e(Throwable e) {
        if (isDebug()) {
            StackTraceElement stackTrace = e.getStackTrace()[0];
            String message = stackTrace.getClassName() + "." + stackTrace.getMethodName() + ":" + stackTrace.getLineNumber() + " " + e.getMessage();
            Logger log = LoggerFactory.getLogger(buildTag());
            log.warn(message);
        }
    }

    private LogUtil() {}

}
