package cn.jpush.alertme.factory.common;


import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

/**
 * Spring Bean Factory
 * Created by ZeFanXie on 14-12-19.
 */
public class SpringBeanFactory {
    private static ApplicationContext act = null;

    public static ApplicationContext getCurrentContext() {
        if (act == null) {
            act = ContextLoader.getCurrentWebApplicationContext();
        }
        return act;
    }


    public static <T> T getBean(Class<T> cls) {
        String simpleName = cls.getSimpleName();
        String className = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1, simpleName.length());
        return SpringBeanFactory.getBean(cls, className);
    }

    public static <T> T getBean(Class<T> cls, String className) {
        try {
            return (T) SpringBeanFactory.getCurrentContext().getBean(className);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }


}
