package cn.jpush.alertme.factory.common.http;

import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import cn.jpush.api.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Http request and response process method
 * Created by ZeFanXie on 14-12-19.
 */
public class HttpContextHelper {
    private static final Logger Log = LoggerFactory.getLogger(HttpContextHelper.class);


    public static boolean isFormDingClient(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (!StringUtil.isEmpty(authorization)) {
            String[] authorizationArr = authorization.split(" ");
            if (authorizationArr.length == 2) {
                String authCode;
                try {
                    authCode = new String(Base64.decode(authorizationArr[1].toCharArray()));
                } catch (IOException e) {
                    return false;
                }
                String[] authCodeArr = authCode.split(":");
                if (authCodeArr.length == 2) {
                    return !StringUtil.isEmpty(authCodeArr[0]);
                }
            }
        }
        return false;
    }

    public static String buildBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    }

    public static void writeResponse(HttpServletResponse response, BaseResponse body) {
        try {
            response.addHeader("Access-Control-Allow-origin", "*");
            response.setStatus(body.getHttpCode());
            response.setContentType("application/json");
            ServletOutputStream out = response.getOutputStream();
            String respStr = JsonUtil.toJson(body);
            out.write(respStr.getBytes());
            out.close();
            Log.debug("[writeResponse] " + respStr);
        } catch (IOException e) {
            Log.error("[writeResponse] Response Fail, content:" + response);
        }
    }

    public static void writeHTML(int status, HttpServletResponse response, String html) {
        try {
            response.setStatus(status);
            response.setContentType("text/html");
            ServletOutputStream out = response.getOutputStream();
            out.write(html.getBytes());
            out.close();
            Log.debug("[writeHTML] Write Html, length:" + html.length());
        } catch (IOException e) {
            Log.error("[writeHTML] Write Html Fail, content:" + response);
        }

    }

    public static void writeHTML(HttpServletResponse response, String html) {
        writeHTML(200, response, html);
    }

    public static void writeSuccessResponse(HttpServletResponse response) {
        writeResponse(response, RightResponse.SUCCESS);
    }

    /**
     * 服务异常
     * @param response
     */
    public static void writeBadServerResponse(HttpServletResponse response) {
        writeResponse(response, ErrorResponse.BAD_SERVER);
    }

    /**
     * 无效的参数
     * @param response
     * @param errorMsg
     */
    public static void writeInvalidParameterResponse(HttpServletResponse response, String errorMsg) {
        writeResponse(response, new ErrorResponse(1002, errorMsg));
    }

    /**
     * 返回错误提醒信息(将会在Client端展示)
     * @param response
     * @param tipMessage
     */
    public static void writeTipMessageResponse(HttpServletResponse response, String tipMessage) {
        writeResponse(response, new ErrorResponse(1001, tipMessage));
    }



    public static void writeMessageResponse(HttpServletResponse response, String message) {
        writeResponse(response, new RightResponse(RightResponse.MESSAGE_RESPONSE, message));
    }


    private HttpContextHelper() {
    }
}
