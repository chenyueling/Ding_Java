package cn.jpush.alertme.factory.common;

import cn.jpush.alertme.factory.common.http.HttpContextHelper;
import cn.jpush.alertme.factory.service.NotifyProcessService;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.LogUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import com.google.gson.JsonSyntaxException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Ful Base Resource
 * Created by ZeFanXie on 14-12-16.
 */
public abstract class BaseResource {
    // 历史原因,现已废弃
    protected Logger getLog() {return null;}
    protected abstract String getTag();

    @Resource
    protected NotifyProcessService notifyProcessService;

    public BaseResource() {
        try {
            initQuartz();
        } catch (SchedulerException e) {
            LogUtil.e(getTag() + " Quartz Init Fail:" + e.getMessage(), e);
        }
    }

    @POST
    @Path("/")
    public void processRequest(@Context HttpServletRequest request, @Context HttpServletResponse resp) {
        NotifyForm dataForm;
        try {
            String reqData = StringUtil.formatInputStream(request.getInputStream());
            dataForm = JsonUtil.format(reqData, NotifyForm.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (notifyProcessService == null) {
            notifyProcessService = SpringBeanFactory.getBean(NotifyProcessService.class);
        }
        boolean isOk = false;
        switch (dataForm.action) {
            case "ACTION_SERVICE_CREATE":
                isOk = onServiceCreate(resp, dataForm.sid, dataForm.api_secret, dataForm.title, dataForm.getSData(), dataForm.getCData());
                break;
            case "ACTION_SERVICE_UPDATE":
                isOk = onServiceUpdate(resp, dataForm.sid, dataForm.title, dataForm.getSData(), dataForm.getCData());
                break;
            case "ACTION_CLIENT_SERVICE_CREATE":
                isOk = onClientServiceCreate(resp, dataForm.sid, dataForm.cid, dataForm.getData());
                break;
            case "ACTION_SERVICE_FOLLOWED_CHANGE":
                isOk = onFollowedChange(resp, dataForm.sid, dataForm.cid, dataForm.followed);
                break;
            case "ACTION_SERVICE_DING":
                isOk = onDing(resp, dataForm.sid, dataForm.cid, dataForm.push_token);
                break;
            default:
                LogUtil.e(String.format("Receive a unhook action %s", dataForm.action));
        }
        if (isOk) {
            HttpContextHelper.writeSuccessResponse(resp);
        }
    }



    protected boolean onDing(HttpServletResponse resp, String sid, String cid, String pushToken) {
        return true;
    }

    protected boolean onFollowedChange(HttpServletResponse resp, String sid, String cid, Long followed) {
        return true;
    }

    protected boolean onClientServiceCreate(HttpServletResponse resp, String sid, String cid, Map<String, String> data) {
        notifyProcessService.saveClientService(sid, cid, data);
        return true;
    }

    protected boolean onServiceUpdate(HttpServletResponse resp, String sid, String title, Map<String, String> sData, List<CData> cData) {
        notifyProcessService.updateService(sid, title, sData, cData);
        return true;
    }

    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        notifyProcessService.saveService(sid, apiSecret, title, sData, cData, getTag());
        return true;
    }

    protected void initQuartz() throws SchedulerException {

    }



    public static class NotifyForm {
        public String action;
        public String title;
        public String api_secret;
        public String sid;
        public String cid;
        public Long followed;
        public String push_token;
        public Location location;

        private String data;
        private String s_data;
        private List<CData> c_data;
        private Map<String, String> _data;
        private Map<String, String> _s_data;

        public Map<String, String> getData() {
            if (_data == null) {
                if (!StringUtil.isEmpty(data)) {
                    try {
                        _data = JsonUtil.formatToMap(data);
                    } catch (JsonSyntaxException e) {
                        _data = new HashMap<>();
                    }
                } else {
                    _data = new HashMap<>();
                }
            }
            return _data;
        }

        public Map<String, String> getSData() {
            if (_s_data == null) {
                if (!StringUtil.isEmpty(s_data)) {
                    try {
                        _s_data = JsonUtil.formatToMap(s_data);
                    } catch (JsonSyntaxException e) {
                        _s_data = new HashMap<>();
                    }
                } else {
                    _s_data = new HashMap<>();
                }
            }
            return _s_data;
        }

        public List<CData> getCData() {
            if (c_data == null) {
                c_data = new ArrayList<>();
            }
            return c_data;
        }

    }

    public static class CData {
        public String cid;

        private String data;
        private Map<String, String> _data;
        public Map<String, String> getData() {
            if (_data == null) {
                if (!StringUtil.isEmpty(data)) {
                    try {
                        _data = JsonUtil.formatToMap(data);
                    } catch (JsonSyntaxException e) {
                        _data = new HashMap<>();
                    }
                } else {
                    _data = new HashMap<>();
                }
            } else {
                return _data;
            }
            return _data;
        }
    }

    public static class Location {
        public String lng;
        public String lat;
    }



}
