package cn.jpush.alertme.factory.plugins.push;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.http.HttpContextHelper;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.plugins.push.article.PushArticleService;
import cn.jpush.alertme.factory.plugins.push.article.PushArticleServiceImpl;
import cn.jpush.alertme.factory.plugins.push.text.PushTextService;
import cn.jpush.alertme.factory.plugins.push.text.PushTextServiceImpl;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by chenyueling on 2015/1/22.
 */

@Path("/api/push")
public class PushResource extends BaseResource{


    public static final String Tag = "Push";
    private static final Logger Log = LoggerFactory.getLogger(PushResource.class);


    private PushArticleService pushArticleService;

    private PushTextService pushTextService;

    @POST
    @Path("text")
    public void pushText(@Context HttpServletRequest request, @Context HttpServletResponse resp) {
        PushForm dataForm = null;
        try {
            String reqData = StringUtil.formatInputStream(request.getInputStream());
            dataForm = JsonUtil.format(reqData, PushForm.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            pushTextService = SpringBeanFactory.getBean(PushTextServiceImpl.class);
            pushTextService.pushText(dataForm.content, dataForm.cid);
            HttpContextHelper.writeSuccessResponse(resp);
        } catch (HttpRequestException e) {
            e.printStackTrace();
            HttpContextHelper.writeBadServerResponse(resp);
        }
    }


    @POST
    @Path("article/link")
    public void pushArticle_link(@Context HttpServletRequest request, @Context HttpServletResponse resp) {
        PushForm dataForm = null;
        try {
            String reqData = StringUtil.formatInputStream(request.getInputStream());
            dataForm = JsonUtil.format(reqData, PushForm.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            pushArticleService = SpringBeanFactory.getBean(PushArticleServiceImpl.class);
            pushArticleService.pushArticleLink(dataForm.title, dataForm.link, dataForm.cid);
            HttpContextHelper.writeSuccessResponse(resp);
        } catch (HttpRequestException e) {
            e.printStackTrace();
            HttpContextHelper.writeBadServerResponse(resp);
        }
    }


    @POST
    @Path("article/html5")
    public void pushArticle_html(@Context HttpServletRequest request, @Context HttpServletResponse resp) {


    }

    @Override
    protected Logger getLog() {
        return Log;
    }

    @Override
    protected String getTag() {
        return Tag;
    }

    public static class PushForm {

        public String link;
        public String title;
        public String content;
        public String sid;
        public String cid;

        public PushForm() {
        }
    }
}
