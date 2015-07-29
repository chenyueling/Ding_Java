package cn.jpush.alertme.factory.plugins.weather;

import cn.jpush.alertme.factory.common.QuartzHelper;
import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2014/12/25.
 */
public class WeatherJob implements Job {

    public ServiceDao serviceDao = null;

    public ClientServiceDao clientServiceDao = null;

    private static final String C_DATA_CITY = "city";

    private static final String S_DATA_DAY = "day";

    private static final String S_DATA_ACTION = "action";

    private static final String[] WORD = {"今天", "明天", "后天", "大后天"};

    private static final String CID = "cid";

    private static final String TEXT = "text";


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);

        List<Service> services = serviceDao.findByTag(WeatherResource.Tag);


        List<Map<String, Object>> pushList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        String flag = date;

        for (Service service : services) {
            String sid = service.getId();
            Map<String, String> s_data = service.getData();
            List<ClientService> clientServiceDaoBySid = clientServiceDao.findBySid(sid);

            for (ClientService clientService : clientServiceDaoBySid) {


                if (Post.isPushed(flag, clientService.getId()) == true) {
                    continue;
                }
                Map<String, String> c_data = clientService.getData();
                String city = c_data.get(WeatherJob.C_DATA_CITY);
                int day = Integer.parseInt(s_data.get(WeatherJob.S_DATA_DAY));
                String action = s_data.get(WeatherJob.S_DATA_ACTION);
                WeatherUtils.Result result = WeatherUtils.getWeatherByCity(city);

                String weather = null;
                String temperature = null;
                switch (day) {
                    //今天
                    case WeatherUtils.WEATHER_DATA_TODAY:
                        weather = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_TODAY).get(WeatherUtils.WEATHER_DATA_WEATHER);
                        temperature = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_TODAY).get(WeatherUtils.WEATHER_DATA_TEMPERATURE);
                        buildPush(weather,temperature,action,city,clientService,pushList);
                        break;
                    //明天
                    case WeatherUtils.WEATHER_DATA_TOMORROW:
                        weather = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_TOMORROW).get(WeatherUtils.WEATHER_DATA_WEATHER);
                        temperature = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_TOMORROW).get(WeatherUtils.WEATHER_DATA_TEMPERATURE);
                        buildPush(weather,temperature,action,city,clientService,pushList);
                        break;
                    //后天
                    case WeatherUtils.WEATHER_DATA_DAY_AFTER_TOMORROW:
                        weather = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_DAY_AFTER_TOMORROW).get(WeatherUtils.WEATHER_DATA_WEATHER);
                        temperature = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_DAY_AFTER_TOMORROW).get(WeatherUtils.WEATHER_DATA_TEMPERATURE);
                        buildPush(weather,temperature,action,city,clientService,pushList);
                        break;
                    //大后天
                    case WeatherUtils.WEATHER_DATA_THREE_DAY_FROM_NOW:
                        weather = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_THREE_DAY_FROM_NOW).get(WeatherUtils.WEATHER_DATA_WEATHER);
                        temperature = result.results.get(0).weather_data.get(WeatherUtils.WEATHER_DATA_THREE_DAY_FROM_NOW).get(WeatherUtils.WEATHER_DATA_TEMPERATURE);
                        buildPush(weather,temperature,action,city,clientService,pushList);
                        break;
                }
            }
        }

        for (Map<String, Object> map : pushList) {

            if (Post.isPushed(flag, (String) map.get(WeatherJob.CID)) == true) {
                continue;
            }
            try {
                //push
                AlertMeClient.pushByCid((String) map.get(WeatherJob.CID)).setText((Text) map.get(WeatherJob.TEXT)).send();
                //add flag
                Post.addPushSet(flag, (String) map.get(WeatherJob.CID));
            } catch (HttpRequestException e) {
                e.printStackTrace();

                try {
                    QuartzHelper.addOneTimesJob(WeatherResource.Tag, 2, this.getClass());
                } catch (SchedulerException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void buildPush(String weather,String temperature,String action,String city,ClientService clientService,List<Map<String, Object>> pushList) {
        if (weather.contains(action)) {
            Text text = new Text();
            StringBuffer content = new StringBuffer();
            content.append(city).append(WeatherJob.WORD[WeatherUtils.WEATHER_DATA_TOMORROW]).append(weather).append(temperature);
            text.setContent(content.toString());
            Map<String, Object> pushData = new HashMap<>();
            pushData.put(WeatherJob.TEXT, text);
            pushData.put(WeatherJob.CID, clientService.getId());
            pushList.add(pushData);
        }
    }
}
