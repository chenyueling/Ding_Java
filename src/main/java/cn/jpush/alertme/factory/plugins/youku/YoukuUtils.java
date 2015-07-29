package cn.jpush.alertme.factory.plugins.youku;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.LogUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenyueling on 2015/2/6.
 */
public class YoukuUtils {

    private static final String YOUKUAPI = "https://openapi.youku.com/v2/shows/videos.json";

    private static final String client_id = "f1c02abfd458c53b";

    private static int MAX_RETRY = 5;

    private static int PAGECOUNT = 40;

    public static final String TITLE = "title";
    public static final String LINK = "link";


    public static YKVideos getYkVideos(String show_id, int page, int pageCount) {
        YKVideos ykVideos = null;
        for (int i = 0; ; i++) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("client_id", client_id);
            params.put("show_id", show_id);
            params.put("page", page + "");
            params.put("count", pageCount + "");

            try {
                String jsonStr = NativeHttpClient.doGet(YOUKUAPI, params);
                Gson gson = new Gson();
                ykVideos = gson.fromJson(jsonStr, YKVideos.class);
                if (ykVideos != null) {
                    break;
                }
                if (i > MAX_RETRY) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (i > MAX_RETRY) {
                    break;
                }
            }
        }
        return ykVideos;
    }

    /**
     * 传入一剧集id,获取最新的
     *
     * @param video_id
     * @return
     */
    public static Video getLastUpdate(String video_id) {
        Video video = null;
        try {
            int page = 1;
            YKVideos Ysvideos = getYkVideos(video_id, page, PAGECOUNT);
            int total = Ysvideos.total;
            if (total > PAGECOUNT) {
                page = total / PAGECOUNT + 1;
            }
            Ysvideos = getYkVideos(video_id, page, PAGECOUNT);
            video = Ysvideos.videos.get(Ysvideos.videos.size() - 1);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.toString());
        } finally {
            return video;
        }
    }


    public class YKVideos {
        public int total;
        public ArrayList<Video> videos;


    }

    class Video {
        //视频唯一ID
        public String id;
        //视频标题
        public String title;
        //视频播放链接
        public String link;
        //视频截图
        public String thumbnail;
        //视频时长，单位：秒
        public float duration;
        //视频分类
        public String category;
        //总播放数
        public int view_count;
        //总收藏数
        public int favorite_count;
        //总评论数
        public int comment_count;
        //总顶数
        public int up_count;
        //总踩数
        public int down_count;
        //节目中视频的集数或期数(日期型 使用YYYYMMDD格式查寻）在查询 片花和预告片时为null
        public int stage;
        //节目中视频顺序号
        public int seq;
        //发布时间
        public String published;
        //操作限制 COMMENT_DISABLED: 禁评论 DOWNLOAD_DISABLED: 禁下载
        public ArrayList<String> operation_limit;
        //视频格式 flvhd flv 3gphd 3gp hd hd2
        public ArrayList<String> streamtypes;
        //视频状态
        public String state;
        //编辑推荐标题
        public String rc_title;


    }

    public static void main(String[] args) {
        //  System.out.println(getYkVideos("z358fb9aafb7d11e3a705", 2, PAGECOUNT).videos.get(0).link);
        Video video = getLastUpdate("z358fb9aafb7cd11e3a705");

        System.out.println(video.title);
        System.out.println(video.link);
    }
}
