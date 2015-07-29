package cn.jpush.alertme.factory.util;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Unit Test Util
 * Created by ZeFanXie on 14-12-17.
 */
public class TestUtil {
    private static ApplicationContext applicationContext;
    private static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        return applicationContext;
    }
    public static SqlSession getSqlSession() {
        ApplicationContext applicationContext = TestUtil.getApplicationContext();
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) applicationContext.getBean("sqlSessionFactory");
        return sqlSessionFactory.openSession();
    }

    public static <T> T getSpringBean(Class<T> cls, String className) {
        ApplicationContext applicationContext = TestUtil.getApplicationContext();
        return (T)applicationContext.getBean(className);
    }


    public static ServletInputStream objectToServletInputStream(Object obj) throws UnsupportedEncodingException {
        return new MockServletInputStream(new ByteArrayInputStream(JsonUtil.toJson(obj).getBytes("UTF-8")));
    }

    public static ServletInputStream stringToServletInputStream(String str) throws UnsupportedEncodingException {
        return new MockServletInputStream(new ByteArrayInputStream(str.getBytes("UTF-8")));
    }

    public static class MockServletInputStream extends ServletInputStream {

        private final InputStream sourceStream;


        /**
         * Create a DelegatingServletInputStream for the given source stream.
         * @param sourceStream the source stream (never <code>null</code>)
         */
        public MockServletInputStream(InputStream sourceStream) {
            Assert.notNull(sourceStream, "Source InputStream must not be null");
            this.sourceStream = sourceStream;
        }

        /**
         * Return the underlying source stream (never <code>null</code>).
         */
        public final InputStream getSourceStream() {
            return this.sourceStream;
        }


        public int read() throws IOException {
            return this.sourceStream.read();
        }

        public void close() throws IOException {
            super.close();
            this.sourceStream.close();
        }

    }
}
