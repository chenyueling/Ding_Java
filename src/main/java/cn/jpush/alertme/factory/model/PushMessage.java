package cn.jpush.alertme.factory.model;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;

import java.util.Date;

/**
 * Created by chenyueling on 2015/1/15.
 */
public class PushMessage {
    private String id;
    private String sid;
    private String cid;
    private AlertMeClient.MessageType type;
    private String tag;
    private Date pushTime;
    private String title;
    private String summary;
    private String content;
    private String link;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public AlertMeClient.MessageType getType() {
        return type;
    }

    public void setType(AlertMeClient.MessageType type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }
}
