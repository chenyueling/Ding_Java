package cn.jpush.alertme.factory.common;

import cn.jpush.alertme.factory.util.PropertiesUtil;

public class Config {
    public static boolean IS_DEBUG;
    public static int SERVER_PORT;
    public static String SERVER_VERSION_PATH;
    public static String SERVER_RESOURCE_PATH;
    public static String SERVER_VERSION;

    /*----Redis Config----*/
    public static final String REDIS_SERVER_URL;
    public static final int REDIS_SERVER_PORT;
    public static final int REDIS_POOL_MAX_TOTAL;
    public static final int REDIS_POOL_MAX_IDLE;
    public static final int REDIS_POOL_MAX_WAIT_MILLIS;

    public static final String ALERT_ME_SERVER_HOST;
    public static final String ALERT_ME_NODE_HOST;
    public static final String ALERT_ME_JAVA_HOST;

    public static final String TEMPLATE_RESOURCE_PATH;

    static {
        IS_DEBUG = PropertiesUtil.getBoolean("IS_DEBUG");
        SERVER_PORT = PropertiesUtil.getInt("SERVER_PORT");
        SERVER_VERSION_PATH = PropertiesUtil.getString("SERVER_VERSION_PATH");
        SERVER_RESOURCE_PATH = System.getProperty("user.dir");
        SERVER_VERSION = PropertiesUtil.getString("SERVER_VERSION");
        REDIS_SERVER_URL = PropertiesUtil.getString("REDIS_SERVER_URL");
        REDIS_SERVER_PORT = PropertiesUtil.getInt("REDIS_SERVER_PORT");
        REDIS_POOL_MAX_TOTAL = PropertiesUtil.getInt("REDIS_POOL_MAX_TOTAL");
        REDIS_POOL_MAX_IDLE = PropertiesUtil.getInt("REDIS_POOL_MAX_IDLE");
        REDIS_POOL_MAX_WAIT_MILLIS = PropertiesUtil.getInt("REDIS_POOL_MAX_WAIT_MILLIS");
        ALERT_ME_SERVER_HOST = PropertiesUtil.getString("ALERT_ME_SERVER_HOST");
        ALERT_ME_NODE_HOST = PropertiesUtil.getString("ALERT_ME_NODE_HOST");
        ALERT_ME_JAVA_HOST =PropertiesUtil.getString("ALERT_ME_JAVA_HOST");
        TEMPLATE_RESOURCE_PATH = PropertiesUtil.getString("TEMPLATE_RESOURCE_PATH");
    }

}
