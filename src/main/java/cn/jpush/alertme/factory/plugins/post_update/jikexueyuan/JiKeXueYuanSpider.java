package cn.jpush.alertme.factory.plugins.post_update.jikexueyuan;

import cn.jpush.alertme.factory.common.exception.SpiderRuntimeError;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.plugins.post_update.BasePostUpdateJob;
import cn.jpush.alertme.factory.util.CollectionUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.JobExecutionException;

import java.util.Map;

/**
 * 极客学院 爬虫任务
 * Created by ZeFanXie on 15-1-26.
 */
public class JiKeXueYuanSpider extends BasePostUpdateJob {
    public static final String Tag = "JiKeXueYuan";
    
    @Override
    protected Map<String, String> doSpiderJob() throws SpiderRuntimeError {
        try {
            String response = NativeHttpClient.get("http://www.jikexueyuan.com/course/");
            Document doc = Jsoup.parse(response);
            Element box = doc.getElementsByClass("lesson-infor").get(0);
            Element h2 = box.getElementsByClass("lesson-info-h2").get(0);
            String title = h2.getElementsByTag("a").get(0).html() + "---" + box.getElementsByTag("p").get(0).html();
            String link = h2.getElementsByTag("a").get(0).attr("href");
            String[] idArr = link.split("/");
            String id = idArr[idArr.length - 1].replace(".html", "");
            if (StringUtil.isEmpty(title) || StringUtil.isEmpty(link) || StringUtil.isEmpty(id)) {
                throw SpiderRuntimeError.PARSE_FAIL_ERROR;
            }
            return CollectionUtil.map().put("title", title).put("link", link).put("id", id).build();
        } catch (HttpRequestException e) {
            if (e.getHttpCode() == 403) {
                throw SpiderRuntimeError.ACCESS_REFUSED_ERROR;
            } else {
                throw SpiderRuntimeError.PARSE_FAIL_ERROR;
            }
        }
    }

    @Override
    protected String getTag() {
        return Tag;
    }

    public static void main(String[] args) throws SpiderRuntimeError, JobExecutionException {
        new JiKeXueYuanSpider().execute(null);
    }
}
