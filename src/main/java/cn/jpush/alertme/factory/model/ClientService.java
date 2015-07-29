package cn.jpush.alertme.factory.model;

import cn.jpush.alertme.factory.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Client Service
 * Created by ZeFanXie on 14-12-16.
 */
public class ClientService {
    private String id;
    private Service service;
    private Map<String, String> data;
    private String dataStr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
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

    public Map<String, String> getMergeData() {
        Map<String, String> result = new HashMap<>();
        result.putAll(this.getData());
        result.putAll(this.getService().getData());
        return result;
    }
}
