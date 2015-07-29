package cn.jpush.alertme.factory.plugins.nba;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2014/12/22.
 */
public class NBAResultJob implements Job{
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            List<Map<Object, String>> games = NBAUtils.todayNBAGame();
            //String content = list.get(0).get(NBAUtils.PRODUCT_MANAGER_NEED_GAME_CODE_STYLE);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date());

            for (Map<Object, String> game : games){
                String game_status = game.get(NBAUtils.GAME_STATUS);
                int index = games.indexOf(game);
                String flag = date + "_" + index;
                //System.out.println("NBAResult_  game_status " + game_status + "  flag " +flag) ;
                if("3".equals(game_status) == false || Post.isPushed(flag)){
                    continue;
                }else{
                    Text text = new Text();
                    text.setContent("开始时间" + "\t" + game.get(NBAUtils.GAME_TIME) + "\n" + game.get(NBAUtils.PRODUCT_MANAGER_NEED_GAME_CODE_STYLE));
                   // System.out.println(text.getContent());
                    AlertMeClient.pushByTag(NBAResultResource.Tag).setText(text).send();


                    Post.addPushSet(flag);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //这种不断尝试的任务，失败以后是不需要设置重试任务的。
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
    }
}
