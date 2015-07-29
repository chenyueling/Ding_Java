package cn.jpush.alertme.factory.plugins.earth_quake;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by chenyueling on 2015/1/30.
 */
public class EarthQuakeJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        CENCUtils.Result recentEarthQuakeInfo = CENCUtils.getRecentEarthQuakeInfo("0");
        CENCUtils.Result.Item item = null;
        if (recentEarthQuakeInfo != null && recentEarthQuakeInfo.shuju.size() > 0) {
            item = recentEarthQuakeInfo.shuju.get(0);
        }else{
            return;
        }

        if(Post.isPushed(item.id) == true){
            return;
        }

        StringBuffer sb = new StringBuffer();
        String content = sb.append(item.O_TIME).append(item.LOCATION_C).append("发生").append(item.M).append("级地震").toString();
        Text text = new Text();
        text.setContent(content);
        try {
            AlertMeClient.pushByTag(EarthQuakeResource.Tag).setText(text).send();
            Post.addPushSet(item.id);
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CENCUtils.Result recentEarthQuakeInfo = CENCUtils.getRecentEarthQuakeInfo("0");
    }
}
