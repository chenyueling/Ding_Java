package cn.jpush.alertme.factory.common.ding;

import cn.jpush.alertme.factory.util.StringUtil;

/**
 * Push Article Model
 * Created by ZeFanXie on 14-12-18.
 */
public class Article {
    private String title;
    private String summary;
    private String link;

    public Article() {}

    public Article(String title, String summary, String link) {
        this.title = title;
        this.summary = summary;
        this.link = link;
    }

    public boolean validate() {
        return !StringUtil.isEmpty(title) && !StringUtil.isEmpty(link);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
