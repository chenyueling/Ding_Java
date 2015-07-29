package cn.jpush.alertme.factory.plugins.push.article;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import org.springframework.stereotype.Component;

/**
 * Created by chenyueling on 2015/1/22.
 */

@Component
public class PushArticleServiceImpl implements PushArticleService{


    @Override
    public void pushArticleLink(String title, String link, String cid) throws HttpRequestException {
        Article article = new Article();
        article.setTitle(title);
        article.setLink(link);
        AlertMeClient.pushByCid(cid).setArticle(article).send();
    }

    @Override
    public void pushArticleHtml5(String title, String content, String cid) {
        //TODO
    }
}
