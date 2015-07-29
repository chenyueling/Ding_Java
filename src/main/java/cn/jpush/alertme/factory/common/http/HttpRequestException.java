package cn.jpush.alertme.factory.common.http;

import java.util.Map;

/**
 * Created by ZeFanXie on 14-12-18.
 */
public class HttpRequestException extends Exception{
    private int httpCode;
    private String context;
    private Map<String, String> requestInfo;

    public HttpRequestException(String message, int httpCode, String context, Map<String, String> data) {
        super(message);
        this.httpCode = httpCode;
        this.context = context;
        this.requestInfo = data;
    }


    public HttpRequestException(String message, Throwable e, Map<String, String> data) {
        super(message, e);
        this.requestInfo = data;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s %s", this.getMessage(), requestInfo.get("method"), requestInfo.get("url"), requestInfo.get("content"), httpCode, context);
    }

    public boolean isForbidden() {
        return httpCode == 403;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Map<String, String> getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(Map<String, String> requestInfo) {
        this.requestInfo = requestInfo;
    }
}
