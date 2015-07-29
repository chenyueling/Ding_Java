package cn.jpush.alertme.factory.plugins.tudou;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chenyueling on 2014/12/18.
 */
public class TudouUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TudouUtils.class);

    private TudouUtils() {

    }

    public static final String ACODE = "{acode}";

    public static final String ICODE = "{icode}";

    private static final String GET_ALBUN_URL = "http://www.tudou.com/tvp/alist.action?a={acode}";

    private static final String GET_ALBUM_ITEM_DETAIL_URL = "http://www.tudou.com/tvp/getItemInfo.action?ic={idcode}";

    //播放某一专辑下的某一集
    private static final String PLAY_ITEM = "http://www.tudou.com/albumplay/{acode}/{icode}.html";

    /**
     * 专辑id
     *
     * @param albumId
     */
    public static Album getAlbumById(String albumId) throws IOException, HttpRequestException {
        String uri = TudouUtils.GET_ALBUN_URL.replace(TudouUtils.ACODE, albumId);
        String json = NativeHttpClient.get(uri);
        Gson gson = new Gson();
        Album album = gson.fromJson(json, Album.class);
        return album;
    }

    /**
     * 根据 专辑码 ，与集数码，获取视频播放地址
     * @param acode
     * @param icode
     * @return
     */
    public static String getPlayUrl(String acode,String icode){
        return TudouUtils.PLAY_ITEM.replace(TudouUtils.ACODE,acode).replace(TudouUtils.ICODE,icode);
    }

    public static void main(String[] args) throws IOException, HttpRequestException {
        System.out.println(getAlbumById("115971").items.get(615-1).kw);
    }


    public class Album {
        public String prd;
        public ArrayList<Item> items;
        public String updateIid;
        public String albumType;

        public class Item {
            public String kw;
            public String scale;
            public String iid;
            public String acode;
            public String tict;
            public String hd;
            public String aid;
            public String pic;
            public String playTimes;
            public String cid;
            public String mediaType;
            public String olh;
            public String time;
            public String cdn;
            public String oid;
            public String pt;
            public String dl;
            public String olw;
            public String comments;
            public String icode;
            public String tvcCode;
            public String vcode;

        }
    }


    public class ItemDetail {
        public String ocode;
        public String kw;
        public String desc;
        public String onic;
        public String oname;
        public String iid;
        public String tag;
        public String curl;
        public String hd;
        public String pic;
        public String cid;
        public String albumurl;
        public String time;
        public String oid;
        public String pt;
        public String dl;
        public String cname;
        public String tvcCode;
        public String vcodel;
        public String icode;

    }
}
