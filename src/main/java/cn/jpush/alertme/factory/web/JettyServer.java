package cn.jpush.alertme.factory.web;

import cn.jpush.alertme.factory.common.Config;
import cn.jpush.alertme.factory.common.QuartzHelper;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

/**
 * Jetty Server
 * Created by ZeFanXie on 14-12-16.
 */
public class JettyServer {
    private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);

    public static void startServer() throws Exception {
        int port = Config.SERVER_PORT;

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(Config.SERVER_VERSION_PATH);
        server.setHandler(context);
        context.setResourceBase(Config.SERVER_RESOURCE_PATH);
        LOG.debug("[main] Service Resource path : " + Config.SERVER_RESOURCE_PATH);
        context.setHandler(new ResourceHandler());

        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "cn.jpush.alertme.factory.plugins");//Set the package where the services reside
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");

        context.setInitParameter("contextConfigLocation", "classpath:applicationContext.xml");
        ContextLoaderListener listener = new ContextLoaderListener();
        context.addEventListener(listener);

        // REST
        context.addServlet(sh, "/*");

        LOG.info("Alert-Me Factory is started. Version:" + Config.SERVER_VERSION + ", Port:" + port);

        server.start();

        // init quartz
        QuartzHelper.init();

        server.join();


    }

    public static void main(String[] args) throws Exception {
        startServer();
    }

}
