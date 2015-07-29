package cn.jpush.alertme.factory.plugins.push.text;

import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Text;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by chenyueling on 2015/1/26.
 */
@Component
public class PushTextServiceImpl implements PushTextService {

    private static final Logger Log = LoggerFactory.getLogger(PushTextServiceImpl.class);

    @Override
    public void pushText(String content, String cid) throws HttpRequestException {
        try {
            Text text = new Text();
            text.setContent(content);
            AlertMeClient.pushByCid(cid).setText(text).send();
        } catch (Exception e) {
            Log.error("PushTextServiceImpl ERROR");
            e.printStackTrace();
        }
    }
}
