package cn.jpush.alertme.factory.common.http;

import cn.jpush.alertme.factory.util.JsonUtil;

/**
 * Base Response
 * Created by ZeFanXie on 14-12-19.
 */
public class BaseResponse {
    protected transient int httpCode = 200;

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
