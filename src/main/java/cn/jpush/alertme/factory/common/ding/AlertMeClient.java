package cn.jpush.alertme.factory.common.ding;

import cn.jpush.alertme.factory.common.Config;
import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.dao.FailureServiceStore;
import cn.jpush.alertme.factory.dao.PushMessageDao;
import cn.jpush.alertme.factory.dao.ServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.model.PushMessage;
import cn.jpush.alertme.factory.model.Service;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import cn.jpush.api.utils.Base64;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Alert Me Request Methods
 * Created by ZeFanXie on 14-12-18.
 */
public class AlertMeClient {
    private static final Logger Log = LoggerFactory.getLogger(AlertMeClient.class);

    private static final String PUSH_URL = Config.ALERT_ME_SERVER_HOST + "/service/{sid}/{cid}/push";

    private static ServiceDao serviceDao = null;
    private static ClientServiceDao clientServiceDao = null;

    private static PushMessageDao pushMessageDao = null;

    private static ServiceDao getServiceDao() {
        if (serviceDao == null) {
            serviceDao = SpringBeanFactory.getBean(ServiceDao.class);
        }
        return serviceDao;
    }

    private static ClientServiceDao getClientServiceDao() {
        if (clientServiceDao == null) {
            clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
        }
        return clientServiceDao;
    }

    private static PushMessageDao getPushMessageDao() {
        if (pushMessageDao == null) {
            // 为了实现强制提交, 此处从从spring取实例, 改用直接从MyBatis实例化实例
            SqlSession session = SpringBeanFactory.getBean(SqlSessionFactory.class).openSession();
            pushMessageDao = session.getMapper(PushMessageDao.class);
            pushMessageDao.setCharsetToUtf8mb4();
            session.commit();
        }
        return pushMessageDao;
    }

    public static void setTextResource(ServiceDao serviceDao, ClientServiceDao clientServiceDao) {
        AlertMeClient.serviceDao = serviceDao;
        AlertMeClient.clientServiceDao = clientServiceDao;
    }

    /**
     * 推送给该Tag下所有子服务
     *
     * @param tag service tag
     * @return
     */
    public static Builder pushByTag(String tag) {
        return new Builder(PushType.TAG, null, null, tag, null, getServiceDao(), getClientServiceDao());
    }

    /**
     * 推送给该SID下所有子服务
     *
     * @param sid service id
     * @return
     */
    public static Builder pushBySid(String sid) {
        return new Builder(PushType.SID, sid, null, null, null, getServiceDao(), getClientServiceDao());
    }

    /**
     * 推送给指定子服务
     *
     * @param cid client service id
     * @return
     */
    public static Builder pushByCid(String cid) {
        return new Builder(PushType.CID, null, cid, null, null, getServiceDao(), getClientServiceDao());
    }

    /**
     * 更具 pushToken 推送给 指定用户
     *
     * @param cid       client service id
     * @param pushToken push token
     * @return
     */
    public static Builder pushByPushToken(String cid, String pushToken) {
        return new Builder(PushType.PUSH_TOKEN, null, cid, null, pushToken, getServiceDao(), getClientServiceDao());
    }

    public static enum MessageType {
        DING,
        TEXT,
        ARTICLE
    }

    public static enum PushType {
        TAG,
        SID,
        CID,
        PUSH_TOKEN
    }


    public static class Builder {
        private MessageType messageType;
        private PushType pushType;
        private String sid;
        private String cid;
        private String tag;
        private String pushToken;
        private Article article;
        private Text text;

        private ServiceDao serviceDao;
        private ClientServiceDao clientServiceDao;

        public Builder(PushType pushType,
                       String sid,
                       String cid,
                       String tag,
                       String pushToken,
                       ServiceDao serviceDao,
                       ClientServiceDao clientServiceDao) {
            this.pushType = pushType;
            this.sid = sid;
            this.cid = cid;
            this.tag = tag;
            this.pushToken = pushToken;
            this.serviceDao = serviceDao;
            this.clientServiceDao = clientServiceDao;
        }

        public Builder setDing() {
            this.messageType = MessageType.DING;
            return this;
        }

        public Builder setArticle(Article article) {
            this.messageType = MessageType.ARTICLE;
            this.article = article;
            return this;
        }

        public Builder setText(Text text) {
            this.messageType = MessageType.TEXT;
            this.text = text;
            return this;
        }

        public void send() throws HttpRequestException {
            if (messageType == null) {
                throw new IllegalArgumentException("MessageType must be set");
            }

            switch (pushType) {
                case TAG:
                    pushByTag();
                    break;
                case SID:
                    pushBySid();
                    break;
                case CID:
                    pushByCid();
                    break;
                case PUSH_TOKEN:
                    pushByPushToken();
                    break;
                default:
                    // Could not have happened, ignore
            }

        }

        private void pushByTag() throws HttpRequestException {
            List<Service> services = serviceDao.findByTag(this.tag);
            List<ClientService> pushList = new ArrayList<>();
            for (Service service : services) {
                pushList.addAll(clientServiceDao.findBySid(service.getId()));
            }

            for (ClientService cs : pushList) {
                this.push(cs, null);
            }
        }

        private void pushBySid() throws HttpRequestException {
            List<ClientService> pushList = clientServiceDao.findBySid(this.sid);
            for (ClientService cs : pushList) {
                this.push(cs, null);
            }
        }

        private void pushByCid() throws HttpRequestException {
            ClientService cs = clientServiceDao.findById(this.cid);
            if (cs == null) {
                Log.error(String.format("Cid:%s Not Fount, Push Fail", this.cid));
                return;
            }
            this.push(cs, null);
        }

        private void pushByPushToken() throws HttpRequestException {
            ClientService cs = clientServiceDao.findById(this.cid);
            this.push(cs, pushToken);
        }

        private void push(ClientService clientService, String pushToken) throws HttpRequestException {

            Map<String, Object> payload = new HashMap<>();
            payload.put("platform", "all");
            if (StringUtil.isEmpty(pushToken)) {
                payload.put("audience", "all");
            } else {
                payload.put("audience", new String[]{pushToken});
            }

            switch (messageType) {
                case DING:

                    payload.put("type", MessageType.DING.name());
                    break;
                case ARTICLE:
                    if (!article.validate()) {
                        throw new IllegalArgumentException("Invalid Parameter 'article'");
                    }
                    payload.put("type", MessageType.ARTICLE.name());
                    payload.put("article", article);
                    break;
                case TEXT:
                    if (!text.validate()) {
                        throw new IllegalArgumentException("Invalid Parameter 'text'");
                    }
                    payload.put("type", MessageType.TEXT.name());
                    payload.put("text", text);
                    break;
                default:
                    // Could not have happened, ignore
            }
            String content = JsonUtil.toJson(payload);
            Service service = clientService.getService();
            String authCode = "Basic " + new String(Base64.encode((service.getId() + ":" + service.getApiSecret()).getBytes()));
            String url = PUSH_URL.replace("{sid}", service.getId()).replace("{cid}", clientService.getId());


            try {
                NativeHttpClient.request(url)
                        .setPost()
                        .setContent(content)
                        .addHeader("Authorization", authCode)
                        .send();
                
            } catch (HttpRequestException e) {
                if (e.isForbidden()) {
                    FailureServiceStore.addFaulureService(clientService);
                }
                throw e;
            }


            
            PushMessage pushMessage = new PushMessage();
            pushMessage.setId(UUID.randomUUID().toString());
            pushMessage.setSid(clientService.getService().getId());
            pushMessage.setCid(clientService.getId());
            pushMessage.setType(messageType);
            pushMessage.setTag(tag);
            pushMessage.setPushTime(new Date());
            if (article != null) {
                pushMessage.setTitle(article.getTitle());
                pushMessage.setSummary(article.getSummary());
                pushMessage.setLink(article.getLink());
            }
            if (text != null) {
                pushMessage.setContent(text.getContent());
            }
            // save message
            pushMessageDao = getPushMessageDao();
            pushMessageDao.save(pushMessage);

            Log.debug(String.format("Push Success: sid:%s, cid:%s, content:%s", service.getId(), clientService.getId(), content));
        }

    }

}
