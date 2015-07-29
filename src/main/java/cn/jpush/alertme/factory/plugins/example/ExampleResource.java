package cn.jpush.alertme.factory.plugins.example;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.FreeMarkerHelper;
import cn.jpush.alertme.factory.common.exception.ServiceException;
import cn.jpush.alertme.factory.common.http.HttpContextHelper;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

/**
 * Test Example
 * Created by ZeFanXie on 14-12-17.
 */
@Path("/example")
public class ExampleResource extends BaseResource {


    @Path("/")
    @GET
    public void example(@Context HttpServletRequest request, @Context HttpServletResponse resp) {
        HttpContextHelper.writeSuccessResponse(resp);
    }

    @Path("/html")
    @GET
    public void freeMarker(@Context HttpServletRequest request, @Context HttpServletResponse resp) {
        try {
            String html = FreeMarkerHelper.createHtml(null, "test.html", request.getServletContext());
            HttpContextHelper.writeHTML(resp, html);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Logger getLog() {
        return null;
    }

    @Override
    protected String getTag() {
        return null;
    }
}
