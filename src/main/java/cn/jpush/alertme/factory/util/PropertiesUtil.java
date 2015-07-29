package cn.jpush.alertme.factory.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by ZeFanXie on 14-12-23.
 */
public class PropertiesUtil {
    private static Properties prop = new Properties();
    static {
        try {
            InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream("config.properties");
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        return prop.getProperty(key).trim();
    }

    public static int getInt(String key) {
        return Integer.valueOf(prop.getProperty(key).trim());
    }

    public static boolean getBoolean(String key) {
        String value = prop.getProperty(key).trim();
        return "true".equals(value) || "TRUE".equals(value) || "1".equals(value);
    }



    private PropertiesUtil() {}
}
