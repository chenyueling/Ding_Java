package cn.jpush.alertme.factory.plugins.push.text;

import cn.jpush.alertme.factory.common.http.HttpRequestException;

/**
 * Created by chenyueling on 2015/1/26.
 */
public interface PushTextService {
    public void pushText(String content, String cid) throws HttpRequestException;
}
