package cn.jpush.alertme.factory.plugins.lofter;


import cn.jpush.alertme.factory.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenyueling on 2014/12/23.
 */
public class LofterUtils {

    private static String URL = "http://www.lofter.com/dwr/call/plaincall/TagBean.search.dwr";

    public static final String LINK = "LINK";

    public static final String TITLE = "TITLE";

    private static final String params = "callCount=1\n" +
            "scriptSessionId=${scriptSessionId}187\n" +
            "httpSessionId=\n" +
            "c0-scriptName=TagBean\n" +
            "c0-methodName=search\n" +
            "c0-id=0\n" +
            "c0-param0=string:{tag}\n" +
            "c0-param1=number:0\n" +
            "c0-param2=string:\n" +
            "c0-param3=string:new\n" +
            "c0-param4=boolean:false\n" +
            "c0-param5=number:0\n" +
            "c0-param6=number:20\n" +
            "c0-param7=number:0\n" +
            "c0-param8=number:0\n" +
            "batchId=416050";

    public static void main(String[] args) {
        System.out.println(getArticleByTag("旅行"));
    }

    /**
     *根据lofter 分类标签获取文章列表
     * @param tag
     * @return
     */
    public static List<Map<String, String>> getArticleByTag(String tag) {

        List<Map<String, String>> list = new ArrayList<>();

        try {
            Map<String, Object> params = new HashMap<>();
            tag = URLEncoder.encode(tag, "utf8");

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "*/*");
            //这一条语句 不能要，否者返回的字符会被压缩
            //headers.put("Accept-Encoding", "gzip, deflate");
            headers.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
            headers.put("Cache-Control", "max-age=0");
            headers.put("Connection", "keep-alive");
            headers.put("Content-Type", "text/plain");
            headers.put("Host", "www.lofter.com");
            headers.put("Origin", "http://www.lofter.com");
            headers.put("Referer", String.format("http://www.lofter.com/tag/%s?first=", tag));

            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            for (String s : headers.keySet()) {
                conn.setRequestProperty(s, headers.get(s));
            }

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String postParams = LofterUtils.params.replace("{tag}", tag);
            conn.getOutputStream().write(postParams.getBytes());
            InputStream inputStream = conn.getInputStream();
            String dwr_unicode = formatInputStream(inputStream);
            String dwr = dwr = StringUtil.unicodeToUtf8(dwr_unicode);

            System.out.println(dwr);
            // dwr.matches("http://.*?.lofter.com/post/.*?_.*?");
            Pattern p_link = Pattern.compile("http://.*?.lofter.com/post/.*?_.*?(?=\")");
            //noticeLinkTitle=.*?;
            Pattern p_title = Pattern.compile("(?<=noticeLinkTitle=\").*?(?=\")");
            Matcher matcher_link = p_link.matcher(dwr);
            Matcher matcher_title = p_title.matcher(dwr);

            while (matcher_link.find() && matcher_title.find()) {
                Map<String, String> map = new HashMap<>();
                map.put(LofterUtils.LINK, matcher_link.group());
                map.put(LofterUtils.TITLE, matcher_title.group());
                list.add(map);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    private static String formatInputStream(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
