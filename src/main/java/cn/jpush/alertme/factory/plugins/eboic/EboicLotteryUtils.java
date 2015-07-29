package cn.jpush.alertme.factory.plugins.eboic;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by chenyueling on 2014/9/9.
 */
public class EboicLotteryUtils {

    private static Logger LOG = LoggerFactory.getLogger(EboicLotteryUtils.class);


    private static final String api = "http://f.eboic.com/{xxx}.json";

    //双色球
    private static final String ssq = "ssq";

    //超级大乐透
    private static final String dlt = "dlt";

    //排列三
    private static final String pl3 = "pl3";

    //七乐彩
    private static final String qlc = "qlc";

    //四场进球彩
    private static final String zcjqc = "zcjqc";

    //福彩3d
    private static final String fc3d = "fc3d";

    //排列五
    private static final String pl5 = "pl5";

    //七星彩
    private static final String qxc = "qxc";

    //六场半全场
    private static final String zcbqc = "zcbqc";

    //足彩胜负彩
    private static final String zcsfc = "zcsfc";


    public static Lotterys.Lottery dlt() throws IOException {
        String uri = api.replace("{xxx}", dlt);

        Lotterys lotterys = getLotterys(uri);

        LOG.info("[EboicLotteryUtils dlt] request api url:" + uri);

        if (lotterys.rows > 0) {
            Lotterys.Lottery lottery = lotterys.data.get(0);
            lottery.opencode = lottery.opencode.replace("+", " [") + " (蓝)]";


            return lottery;
        } else {
            LOG.info("[EboicLotteryUtils dlt] request result is null");
            return null;
        }

    }

    public static Lotterys.Lottery ssq() throws IOException {
        String uri = api.replace("{xxx}", ssq);

        Lotterys lotterys = getLotterys(uri);

        LOG.info("[EboicLotteryUtils ssq] request api url:" + uri);

        if (lotterys.rows > 0) {
            Lotterys.Lottery lottery = lotterys.data.get(0);
            lottery.opencode = lottery.opencode.replace("+", " ") + "(蓝)";
            return lottery;
        } else {
            LOG.info("[EboicLotteryUtils ssq] request result is null");
            return null;
        }

    }


    public static Lotterys.Lottery fc3d() throws IOException {
        String uri = api.replace("{xxx}", fc3d);

        Lotterys lotterys = getLotterys(uri);

        LOG.info("[EboicLotteryUtils fc3d] request api url:" + uri);

        if (lotterys.rows > 0) {
            return lotterys.data.get(0);
        } else {
            LOG.info("[EboicLotteryUtils fc3d] request result is null");
            return null;
        }

    }

    public static Lotterys.Lottery qlc() throws IOException {
        String uri = api.replace("{xxx}", qlc);

        Lotterys lotterys = getLotterys(uri);

        LOG.info("[EboicLotteryUtils qlc] request api url:" + uri);

        if (lotterys.rows > 0) {
            Lotterys.Lottery lottery = lotterys.data.get(0);
            lottery.opencode = lottery.opencode.replace("+", " ") + "(蓝)";
            return lottery;
        } else {
            LOG.info("[EboicLotteryUtils qlc] request result is null");
            return null;
        }

    }


    private static Lotterys getLotterys(String uri) throws IOException {
        InputStream inputStream = null;
        Lotterys lotterys = null;
        try {

            String json = NativeHttpClient.get(uri);

            //LOG.info("[EboicLotteryUtils getLotterys] request api result:" + json);
            Gson gson = new Gson();
            lotterys = gson.fromJson(json, Lotterys.class);

        } catch (HttpRequestException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return lotterys;
    }


    public static Lotterys.Lottery getLotteryByType(String type) throws IOException {
        switch (type){
            case "sevenHappyColor" :
                return qlc();
            case "fucai3D" :
                return fc3d();
            case "superLotto":
                return dlt();
            case "doubleChromosphere":
                return ssq();
            default:
                return null;
        }
    }


    /**
     * {
     * "rows": "5",
     * "info": "免费接口随机延迟1-8分钟。购买或试用付费接口加QQ:9564384(注明彩票API)",
     * "code": "ssq",
     * "data": [
     * {
     * "expect": "2014103",
     * "opencode": "03,08,09,10,18,33+04",
     * "opentime": "2014-09-07 21:33:00",
     * "opentimestamp": "1410096780000"
     * },
     * {
     * "expect": "2014102",
     * "opencode": "14,16,21,24,28,31+13",
     * "opentime": "2014-09-04 21:33:00",
     * "opentimestamp": "1409837580000"
     * },
     * {
     * "expect": "2014101",
     * "opencode": "16,18,20,23,24,32+07",
     * "opentime": "2014-09-02 21:33:00",
     * "opentimestamp": "1409664780000"
     * },
     * {
     * "expect": "2014100",
     * "opencode": "01,06,09,10,14,16+11",
     * "opentime": "2014-08-31 21:33:00",
     * "opentimestamp": "1409491980000"
     * },
     * {
     * "expect": "2014099",
     * "opencode": "01,05,10,11,13,32+14",
     * "opentime": "2014-08-28 21:33:00",
     * "opentimestamp": "1409232780000"
     * }
     * ]
     * }
     */
    public class Lotterys {
        public int rows;
        public String info;
        public String code;
        public List<Lottery> data;

        /**
         * json格式
         * {
         * "expect": "2014101",
         * "opencode": "16,18,20,23,24,32+07",
         * "opentime": "2014-09-02 21:33:00",
         * "opentimestamp": "1409664780000"
         * }
         */
        public class Lottery {
            //第XXXXX期彩票
            public String expect;
            //开奖号码
            public String opencode;
            //开奖日期
            public String opentime;
            //开奖时间戳
            public long opentimestamp;

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append(expect).append(" 期开奖").append("开奖号码为 ").append(opencode);
                return sb.toString();
            }
        }


    }

    public static void main(String[] args) throws IOException {
        System.out.println(ssq().toString());
        System.out.println(fc3d().toString());
        System.out.println(qlc().toString());
        System.out.println(dlt().toString());
    }

}
