package cn.jpush.alertme.factory.plugins.finance.bitcoin;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.Service;
import cn.jpush.alertme.factory.plugins.finance.FinanceTip;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import com.google.gson.JsonObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/6.
 */
public class BitCoinTipJob extends FinanceTip implements Job {

    private ServiceDao serviceDao = null;
    private ClientServiceDao clientServiceDao = null;

    private String threshold = null;

    public String price;

    /**
     * 价格状态，高于预期还是低于预期
     */
    public String status;

    private static final String API = "https://www.btctrade.com/coin/rmb/rate.js";

    private static final String S_THRESHOLD = "THRESHOLD";

    @Override
    public String getData() {
        try {
            URL url = new URL(BitCoinTipJob.API);
            InputStream inputStream = url.openStream();
            String json = StringUtil.formatInputStream(inputStream);
            JsonObject jsonObject = JsonUtil.format(json, JsonObject.class);
            price = jsonObject.get("btc").getAsString();
          //  System.out.println(price);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return price;
    }

    @Override
    public String getThreshold() {
       // System.out.println("[@Override getThreshold]" + threshold);
        return threshold;
    }

    public void setThreshold(String threshold) {
       // System.out.println("setThreshold" +threshold);
        this.threshold = threshold;
    }

    /**
     *
     * @param dataF 当前价格
     * @param threasholdF 创建服务时指定的阀值
     * @return
     */
    @Override
    public Text buildPush(float dataF, float threasholdF) {
        Text text = new Text();

        String baseLine = getBaseLine();
        float baseLineF = Float.parseFloat(baseLine);
        //System.out.println("baseLinF"+ baseLineF);
        ///System.out.println("dataF"+ dataF);
        //System.out.println("threasholdF"+ threasholdF);

        if (dataF - baseLineF > threasholdF) {
            text.setContent("当前比特币价格为" + dataF + "元");
            status = "UP";
        } else if (baseLineF - dataF > threasholdF) {
            text.setContent("当前比特币价格为" + dataF + "元");
            status = "DOWN";
        }
        return text;
    }

    @Override
    public String getTag() {
        return BitCoinTipResource.Tag;
    }


    public String baseLine = "";

    public String getBaseLine(){
        if(baseLine == null || "".equals(baseLine)){
            return "0";
        }else{
            return baseLine;
        }
    }

    public static void main(String[] args) {
        new BitCoinTipJob().financeTip();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);

        List<Service> services = serviceDao.findByTag(getTag());

        for (Service service : services) {
            String sid = service.getId();
            List<ClientService> clientServices = clientServiceDao.findBySid(sid);
            for (ClientService clientService : clientServices) {
                BitCoinTipJob bitCoinTipJob = new BitCoinTipJob();
                Map<String, String> c_data = clientService.getData();
                Map<String, String> s_data = service.getData();
                /**
                 * 拿出基线值
                 */
                bitCoinTipJob.setBaseLine(Post.getBaseLinePrice(service.getId(), clientService.getId()));

                bitCoinTipJob.setThreshold(s_data.get(BitCoinTipJob.S_THRESHOLD));
                Text text = null;
                try{
                     text = bitCoinTipJob.financeTip();
                }catch (Exception e){

                }


                //现在不需要验证是否是推送过，当前只需要满足条件就进行推送，唯一需要保存的
                //值是基线值。根据基线值进行判断，是否进行推送，阀值也是后台可以进行设定的，这样感觉还是挺不错的额，
                //现在的问题是，基线值的获取问题，如果后台程序一直在跑，基线值一定是维持在波动的在波动的中间位置，感觉上没有必要
                //按照九点钟的时候去拉去数据，
                //如果决定不使用每日初始基线的方法，那么基线的初始化问题。该如何解决。
                //假设第一次初始化为空值，进行判断，如果是空值记录，那么就将它初始化为零，这时候肯定有一条推送，就在服务创建的时候，
                //然后基线值就出来了。
                //我们把阀值设置在s_data里面
                //boolean isPushed = Post.isPushed(bitCoinTipJob.status, service.getId(), clientService.getId());
                //if (isPushed == true) {
                //  continue;
                //}

                if(text == null || text.validate() == false){
                    continue;
                }

                try {
                    AlertMeClient.pushByCid(clientService.getId()).setText(text).send();
                    Post.setBaseLinePrice(bitCoinTipJob.price, service.getId(), clientService.getId());
                    Post.addPushSet(bitCoinTipJob.status, service.getId(), clientService.getId());
                } catch (HttpRequestException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setBaseLine(String baseLine) {
        this.baseLine = baseLine;
    }
}
