package cn.jpush.alertme.factory.model;

import cn.jpush.alertme.factory.util.JsonUtil;

import java.util.Map;

/**
 * Service
 * Created by ZeFanXie on 14-12-16.
 */
public class Service {
    private String id;
    private String apiSecret;
    private String title;
    private Map<String, String> data;
    private String dataStr;
    private String tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
        this.dataStr = JsonUtil.toJson(data);
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
        this.data = JsonUtil.formatToMap(dataStr);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
