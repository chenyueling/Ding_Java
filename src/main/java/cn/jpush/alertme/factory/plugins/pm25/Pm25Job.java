package cn.jpush.alertme.factory.plugins.pm25;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import cn.jpush.alertme.factory.util.LogUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/29.
 */
public class Pm25Job implements Job {

    private static final String CITY = "city";


    private ServiceDao serviceDao;

    private ClientServiceDao clientServiceDao;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Service> services = serviceDao.findByTag(Pm25Resource.Tag);
        for (Service service : services) {
            String sid = service.getId();
            List<ClientService> clientServices = clientServiceDao.findBySid(sid);
            Map<String, String> sc_data;
            for (ClientService clientService : clientServices) {
                String cid = clientService.getId();
                sc_data = clientService.getMergeData();
                String city = sc_data.get(CITY);
                try {
                    Pm25Util.PM25 pm25 = Pm25Util.getPmAvgByCityName(city);

                    //获取当前的时间戳
                    String todayLevel = Post.getTodayLevel(cid);
                    String lastStamp = Post.getLastStamp(cid);
                    long lastStampL = Long.parseLong(lastStamp);

                    Text text = new Text();
                    String content = pmStatus(todayLevel, lastStampL, city, pm25.pm2_5, cid);
                    if (content == null) {
                        continue;
                    }
                    text.setContent(content);
                    try {
                        AlertMeClient.pushByCid(cid).setText(text).send();
                    } catch (HttpRequestException e) {
                        LogUtil.e("[Pm25Job execute] AlterMeClient error");
                        Post.getTodayLevel(todayLevel);
                        Post.setStamp(cid, lastStampL);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HttpRequestException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Pm25Job() {
        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
    }


    /**
     * @param lastStamp
     * @param city
     * @param pm_25
     * @param cid
     * @return
     */
    public static String pmStatus(String todayLevel, long lastStamp, String city, int pm_25, String cid) {


        //当日整点的时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long newDay = calendar.getTimeInMillis();


        //增加一个上一次推送的,时间戳,判断是否跨过新的一天
        if (newDay >= lastStamp) {
            todayLevel = "0";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(city).append("当前PM2.5值为").append(pm_25);
        int level = Integer.parseInt(todayLevel);
        List<Object> list = new ArrayList<Object>();

        //当获取到的PM值大于用户
        if (pm_25 >= 75 && pm_25 < 115 && level < 1) {
            todayLevel = "1";
            sb.append(",空气质量为轻度污染");
        } else if (pm_25 >= 115 && pm_25 < 150 && level < 2) {
            todayLevel = "2";
            sb.append(",空气质量为中度污染");
        } else if (pm_25 >= 150 && pm_25 < 250 && level < 3) {
            todayLevel = "3";
            sb.append(",空气质量为重度污染");
        } else if (pm_25 >= 250 && level < 4) {
            todayLevel = "4";
            sb.append(",空气质量为严重污染");
        } else {
            return null;
        }
        //这里先设置一次,如果发生异常可以回滚
        Post.setTodayLevel(cid, todayLevel);
        Post.setStamp(cid, System.currentTimeMillis());

        return sb.toString();
    }

    public static void main(String[] args) throws JobExecutionException {
        try {
            Pm25Util.PM25 pm25 = Pm25Util.getPmAvgByCityName("珠海");
            System.out.println(pmStatus("0", 0, pm25.area, pm25.pm2_5, "1"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
    }
}
