package cn.jpush.alertme.factory.plugins.push.article;

import cn.jpush.alertme.factory.common.http.HttpRequestException;

/**
 * Created by chenyueling on 2015/1/22.
 */
public interface PushArticleService {
    public void pushArticleLink(String title,String link,String cid) throws HttpRequestException;

    public void pushArticleHtml5(String title,String content,String cid);
}
