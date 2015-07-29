package cn.jpush.alertme.factory.plugins.air_ticket;

import cn.jpush.alertme.factory.common.RedisFactory;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.List;

/**
 * Air Ticket Dao Method
 * Created by ZeFanXie on 15-1-9.
 */
public class AirTicketStore {
    private static final String AIR_TICKET_KEY = "i:air_ticket:{id}";
    private static final String FLIGHT_SET = "s:air_ticket:{routes}";
    private static final String SUPPORT_CITY_KEY = "s:air_ticket:support.city";
    private static final String AIRPORT_CODE_TABLE_KEY = "h:airport.codes";

    public static void initSupportCityData() {
        String cityStr = "北京,上海,广州,成都,重庆,西安,昆明,深圳,杭州,厦门,长沙,海口,武汉,乌鲁木齐,郑州,三亚,贵阳,南京,青岛,哈尔滨,阿克苏,安庆,阿勒泰,安康,鞍山,阿坝,安顺,阿尔山,阿里,阿拉善右旗,阿拉善左旗,澳门,北京,包头,北海,保山,百色,巴彦淖尔,毕节,博乐,成都,重庆,长沙,长春,常州,常德,长治,赤峰,朝阳,长白山,池州,昌都,长海,大连,丹东,大理,大同,东营,敦煌,达州,达县(达州),大庆,稻城,恩施,鄂尔多斯,二连浩特,额济纳旗,福州,阜阳,佛山,抚远,广州,贵阳,桂林,赣州,广元,格尔木,广汉,固原,甘南(夏河),高雄,杭州,海口,哈尔滨,合肥,呼和浩特,海拉尔,邯郸,黄山,和田,惠州,汉中,黄龙(九寨沟),衡阳,黑河,哈密,黄岩(台州),淮安,河池,海西,恒春,花莲,济南,晋江,揭阳,佳木斯,景洪(西双版纳),锦州,景德镇,九江,济宁,井冈山,吉安(井冈山),嘉峪关,九寨沟,酒泉,加格达奇,鸡西,金昌,金门,嘉义,昆明,喀什,库尔勒,库车,克拉玛依,喀纳斯,康定,凯里,兰州,丽江,拉萨,柳州,洛阳,连云港,临沂,泸州,临沧,连城,黎平,荔波,梁平,林芝,吕梁,六盘水,绿岛,兰屿,牡丹江,绵阳,满洲里,芒市,梅县,漠河,马祖,马公,南京,南宁,宁波,南昌,南阳,南通,南充,那拉提,攀枝花,普洱,屏东,青岛,齐齐哈尔,秦皇岛,衢州,庆阳,且末,黔江,七美,日喀则,日月潭,上海,深圳,沈阳,三亚,石家庄,思茅(普洱),沙市,神农架,韶关,鄯善,天津,太原,通辽,铜仁,台州,塔城,通化,唐山,吐鲁番,腾冲,天水,台北,台南,台东,台中,武汉,乌鲁木齐,温州,无锡,威海,万州,万县(万州),潍坊,武夷山,乌海,乌兰浩特,梧州,文山,芜湖,望安,西安,厦门,西宁,徐州,西双版纳,西昌,兴义,香格里拉,锡林浩特,襄阳(中国),襄樊(襄阳),兴城,夏河,邢台,香港,银川,烟台,延吉,宜昌,义乌,运城,宜宾,榆林,盐城,伊宁,延安,永州,扬州,伊春,玉树,宜春,郑州,珠海,张家界,湛江,芷江,舟山,昭通,遵义,张掖,中卫,张家口";
        String[] cities = cityStr.split(",");
        Jedis client = RedisFactory.getInstance();
        try {
            if (!client.exists(SUPPORT_CITY_KEY)) {
                client.sadd(SUPPORT_CITY_KEY, cities);
            }
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void initAirportCode() {
        String cityStr = "阿里,阿尔山,安庆,阿勒泰,安康,鞍山,安顺,阿克苏,阿拉善左旗,阿拉善右旗,包头,北海,北京,百色,保山,博乐,毕节,巴彦淖尔,长治,池州,长春,常州,昌都,朝阳,常德,长白山,成都,重庆,长沙,赤峰,大同,大连,东营,大庆,丹东,大理,敦煌,达州,稻城,恩施,鄂尔多斯,二连浩特,额济纳旗,佛山,福州,阜阳,抚远,贵阳,桂林,广州,广元,格尔木,赣州,固原,哈密,呼和浩特,黑河,海拉尔,哈尔滨,海口,黄山,杭州,邯郸,合肥,黄龙,汉中,和田,淮安,鸡西,晋江,锦州,景德镇,嘉峪关,井冈山,济宁,九江,佳木斯,济南,加格达奇,金昌,揭阳,喀什,昆明,康定,克拉玛依,库尔勒,库车,喀纳斯,凯里,兰州,洛阳,丽江,荔波,林芝,柳州,泸州,连云港,黎平,连城,拉萨,临沧,临沂,吕梁,芒市,牡丹江,满洲里,绵阳,梅县,漠河,南京,南充,南宁,南阳,南通,南昌,那拉提,宁波,攀枝花,普洱,衢州,黔江,秦皇岛,庆阳,且末,齐齐哈尔,青岛,日喀则,深圳,石家庄,三亚,沈阳,上海,神农架,唐山,铜仁,塔城,腾冲,台州,天水,天津,通辽,吐鲁番,太原,威海,武汉,梧州,文山,无锡,潍坊,武夷山,乌兰浩特,温州,乌鲁木齐,万州,乌海,兴义,西昌,厦门,香格里拉,西安,西宁,襄阳(中国),锡林浩特,西双版纳,徐州,义乌,永州,榆林,扬州,延安,运城,烟台,银川,宜昌,宜宾,宜春,盐城,延吉,玉树,伊宁,伊春,珠海,昭通,张家界,舟山,郑州,中卫,芷江,湛江,遵义,张掖,张家口";
        String codeStr = "NGQ,YIE,AQG,AAT,AKA,AOG,AVA,AKU,AXF,RHT,BAV,BHY,BJS,AEB,BSD,BPL,BFJ,RLK,CSX,JUH,CGQ,CZX,BPX,CHG,CGD,NBS,CTU,CKG,CSX,CIF,DAT,DLC,DOY,DQA,DDG,DLU,DNH,DAX,DCY,ENH,DSN,ERL,EJN,FUO,FOC,FUG,FYJ,KWE,KWL,CAN,GYS,GOQ,KOW,GYU,HMI,HET,HEK,HLD,HRB,HAK,TXN,HGH,HDG,HFE,JZH,HZG,HTN,HIA,JXA,JJN,JNZ,JDZ,JGN,JGS,JNG,JIU,JMU,TNA,JGD,JIC,SWA,KHG,KMG,KGT,KRY,KRL,KCA,KJI,KJH,LHW,LYA,LJG,LLB,LZY,LZH,LZO,LYG,HZH,LCX,LXA,LNJ,LYI,LLV,LUM,MDG,NZH,MIG,MXZ,OHE,NKG,NAO,NNG,NNY,NTG,KHN,NLT,NGB,PZI,SYM,JUZ,JIQ,SHP,IQN,IQM,NDG,TAO,RKZ,SZX,SJW,SYX,SHE,SHA,HPG,TVS,TEN,TCG,TCZ,HYN,THQ,TSN,TGO,TLQ,TYN,WEH,WUH,WUZ,WNH,WUX,WEF,WUS,HLH,WNZ,URC,WXN,WUA,ACX,XIC,XMN,DIG,SIA,XNN,XFN,XIL,JHG,XUZ,YIW,LLF,UYN,YTY,ENY,YCU,YNT,INC,YIH,YBP,YIC,YNZ,YNJ,YUS,YIN,LDS,ZUH,ZAT,DYG,HSN,CGO,ZHY,HJJ,ZHA,ZYI,YZY,ZQZ";
        Jedis client = RedisFactory.getInstance();
        try {
            if (!client.exists(AIRPORT_CODE_TABLE_KEY)) {
                String[] cities = cityStr.split(",");
                String[] codes = codeStr.split(",");
                for (int i=0; i<cities.length; i++) {
                    client.hset(AIRPORT_CODE_TABLE_KEY, cities[i], codes[i]);
                }
            }
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static String getAirportCode(String city) {
        Jedis client = RedisFactory.getInstance();
        try {
            return client.hget(AIRPORT_CODE_TABLE_KEY, city);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static boolean isSupportCity(String city) {
        Jedis client = RedisFactory.getInstance();
        try {
            return client.sismember(SUPPORT_CITY_KEY, city) && client.hexists(AIRPORT_CODE_TABLE_KEY, city);
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } finally {
            RedisFactory.release(client);
        }
    }




    public static boolean isNewAirTicket(AirTicket airTicket) {
        Jedis client = RedisFactory.getInstance();
        try {
            String key = FLIGHT_SET.replace("{routes}", StringUtil.base64(airTicket.getStart() + "+" + airTicket.getEnd()));
            return !client.sismember(key, airTicket.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            return false;
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void saveAirTicket(AirTicket airTicket) {
        Jedis client = RedisFactory.getInstance();
        try {
            client.sadd(FLIGHT_SET.replace("{routes}", StringUtil.base64(airTicket.getStart() + "+" + airTicket.getEnd())), airTicket.getId());
            client.set(AIR_TICKET_KEY.replace("{id}", airTicket.getId()), JsonUtil.toJson(airTicket));
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        } finally {
            RedisFactory.release(client);
        }
    }

    public static void saveAirTicket(List<AirTicket> airTickets) {
        Jedis client = RedisFactory.getInstance();
        try {
            for (AirTicket airTicket : airTickets) {
                client.sadd(FLIGHT_SET.replace("{routes}", StringUtil.base64(airTicket.getStart() + "+" + airTicket.getEnd())), airTicket.getId());
                client.set(AIR_TICKET_KEY.replace("{id}", airTicket.getId()), JsonUtil.toJson(airTicket));
            }
        } catch (JedisConnectionException e) {
            RedisFactory.release(client, true);
            throw e;
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        } finally {
            RedisFactory.release(client);
        }
    }





}
