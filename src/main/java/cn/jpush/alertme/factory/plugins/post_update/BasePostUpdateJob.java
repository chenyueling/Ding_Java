package cn.jpush.alertme.factory.plugins.post_update;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.exception.SpiderRuntimeError;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.util.LogUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;

/**
 * Created by ZeFanXie on 15-1-26.
 */
public abstract class BasePostUpdateJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LogUtil.d("Spider Job Execute");
        try {
            boolean isPushable = isPushable();
            if (!isPushable) {
                LogUtil.d("Is Unable To Push");
                return;
            }
            Map<String, String> post = doSpiderJob();
            if (!isPushed(post)) {
                onUpdate(post);
                addPushTag(post);
            }
        } catch (SpiderRuntimeError e) {
            LogUtil.e("Spider Runtime Error", e);
            e.printStackTrace();
        } catch (Exception e) {
            LogUtil.e("Unknown Exception", e);
            e.printStackTrace();
        }

    }

    


    protected abstract Map<String, String> doSpiderJob() throws SpiderRuntimeError;

    protected void onUpdate(Map<String, String> post) throws HttpRequestException {
        String link = post.get("link");
        String title = post.get("title");

        Article article = new Article();
        article.setTitle(title);
        article.setLink(link);
        AlertMeClient.pushByTag(getTag()).setArticle(article).send();

    }

    /**
     * 判断是否推送过该Post, 因为保险起见, 在未知情况下默认都返回true已推送过
     * @param post *
     * @return *
     */
    protected boolean isPushed(Map<String, String> post) {
        return !(post != null && post.containsKey("id")) || PostUpdateStore.isPushed(getTag(), post.get("id"));
    }

    /**
     * 判断是否可以推送, 默认不做限制, 返回true, 可以在此做推送限制
     * @return *
     */
    protected boolean isPushable() {
        return true;
    }

    /**
     * 将指定的post.id加入已推送的集合中去, 做推送标示
     * @param post
     */
    protected void addPushTag(Map<String, String> post) {
        String id = post.get("id");
        if (!StringUtil.isEmpty(id)) {
            PostUpdateStore.addPushSet(getTag(), post.get("id"));
        }
    }

    protected abstract String getTag();
}
