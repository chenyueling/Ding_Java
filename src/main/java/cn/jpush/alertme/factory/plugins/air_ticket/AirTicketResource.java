package cn.jpush.alertme.factory.plugins.air_ticket;

import cn.jpush.alertme.factory.common.BaseResource;
import cn.jpush.alertme.factory.common.QuartzHelper;
import cn.jpush.alertme.factory.common.http.HttpContextHelper;
import cn.jpush.alertme.factory.util.StringUtil;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;


@Path("/air_ticket")
public class AirTicketResource extends BaseResource {
    private static final Logger Log = LoggerFactory.getLogger(AirTicketResource.class);
    public static final String Tag = "Air_Ticket";
    // 每半小时执行一次
    private static final String cron = "0 */30 * * * ?";

    @Override
    protected boolean onClientServiceCreate(HttpServletResponse resp, String sid, String cid, Map<String, String> data) {
        String startCity = data.get("start_city");
        String endCity = data.get("end_city");
        if (StringUtil.isEmpty(startCity) || !AirTicketStore.isSupportCity(startCity)) {
            HttpContextHelper.writeTipMessageResponse(resp, "暂不支持该城市组合的机票提醒");
            return false;
        }
        if (StringUtil.isEmpty(endCity) || !AirTicketStore.isSupportCity(endCity)) {
            HttpContextHelper.writeTipMessageResponse(resp, "暂不支持该城市组合的机票提醒");
            return false;
        }


        return super.onClientServiceCreate(resp, sid, cid, data);
    }

    @Override
    protected boolean onServiceUpdate(HttpServletResponse resp, String sid, String title, Map<String, String> sData, List<CData> cData) {

        // 验证 Client Field 合法性
        for (CData data : cData) {
            String startCity = data.getData().get("start_city");
            String endCity = data.getData().get("end_city");
            if (StringUtil.isEmpty(startCity) || AirTicketStore.isSupportCity(startCity)) {
                HttpContextHelper.writeInvalidParameterResponse(resp, "Invalid Start City " + startCity);
                return false;
            }
            if (StringUtil.isEmpty(endCity) || !AirTicketStore.isSupportCity(endCity)) {
                HttpContextHelper.writeInvalidParameterResponse(resp, "Invalid Start City " + startCity);
                return false;
            }
        }

        return super.onServiceUpdate(resp, sid, title, sData, cData);
    }

    @Override
    protected boolean onServiceCreate(HttpServletResponse resp, String sid, String apiSecret, String title, Map<String, String> sData, List<CData> cData) {
        // 添加单例定时任务
        try {
            QuartzHelper.addSingleJob(Tag, cron, AirSpiderJob.class);
        } catch (SchedulerException e) {
            Log.error(Tag + " Quartz Init Fail:" + e.getMessage());
        }
        Log.debug(Tag + " Timer Running...");

        // 初始化 城市, 机场 数据
        AirTicketStore.initSupportCityData();
        AirTicketStore.initAirportCode();

        // 验证 Client Field 合法性
        for (CData data : cData) {
            String startCity = data.getData().get("start_city");
            if (StringUtil.isEmpty(startCity) || AirTicketStore.isSupportCity(startCity)) {
                HttpContextHelper.writeInvalidParameterResponse(resp, "Invalid Start City " + startCity);
                return false;
            }
        }

        return super.onServiceCreate(resp, sid, apiSecret, title, sData, cData);
    }

    @Override
    protected Logger getLog() {
        return Log;
    }

    @Override
    protected String getTag() {
        return Tag;
    }
}
