package cn.jpush.alertme.factory.common;

import cn.jpush.alertme.factory.common.exception.ServiceException;
import cn.jpush.alertme.factory.common.http.HttpContextHelper;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import java.util.HashMap;
import java.util.Map;

/**
 * 文章信息渲染页面
 * Created by chenyueling on 2014/12/26.
 */

public abstract class ArticleResource extends BaseResource {

    private static final String GET = "/article/{id}";

    @GET
    @Path("/article/{id}")
    public void showArticle(@Context HttpServletRequest request, @Context HttpServletResponse resp, @PathParam("id") String id) throws ServiceException {
        Map<String, Object> data = new HashMap<>();
        boolean isFromDingClient = HttpContextHelper.isFormDingClient(request);
        data.put("auth", isFromDingClient);
        data.put("basePath", HttpContextHelper.buildBasePath(request));
        try {
            Map<String, String> articleMap = RedisHelper.getArticle(getTag(), id);
            if (articleMap == null || articleMap.isEmpty()) {
                // article not found
                HttpContextHelper.writeHTML(404, resp, FreeMarkerHelper.createErrorPage("404 该页面不存在", request.getServletContext()));
                return;
            }

            data.putAll(articleMap);
            HttpContextHelper.writeHTML(resp, FreeMarkerHelper.createHtml(data, FreeMarkerHelper.ARTICLE_TEMPLATE, request.getServletContext()));
        } catch (ServiceException e) {
            HttpContextHelper.writeHTML(500, resp, FreeMarkerHelper.createErrorPage("500 叮咚遇到了一点问题", request.getServletContext()));
            e.printStackTrace();
        }
    }


    public static String getArticlePath(String id) {
        return ArticleResource.GET.replace("{id}", id);
    }

    @Override
    protected abstract Logger getLog();

    @Override
    protected abstract String getTag();

}