package cn.jpush.alertme.factory.plugins.air_ticket;

import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.ding.AlertMeClient;
import cn.jpush.alertme.factory.common.ding.Article;
import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.dao.ClientServiceDao;
import cn.jpush.alertme.factory.model.ClientService;
import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZeFanXie on 15-1-9.
 */
public class AirSpiderJob implements Job {
    private static final Logger Log = LoggerFactory.getLogger(AirSpiderJob.class);
    private static final String REQUEST_URL = "http://touch.qunar.com/h5/flight/bargainflight?startCity={start}&destCity={end}";
    private static final String BASE_URL = "http://touch.qunar.com/h5/flight/";
    private static final String PRICE_URL = "http://lp.flight.qunar.com/api/qdclowprice?dcity={start}&acity={end}&fromCode={fromCode}&toCode={toCode}&ddate={date}&drange=0&query=search&sort=S1&asc=true&page=1&from=tejia_a_qe&ex_track=&searchType=domestic&per=40&perScrollPage=10";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Log.debug("Air Ticket Spider Execute...");

        ClientServiceDao clientServiceDao = SpringBeanFactory.getBean(ClientServiceDao.class);
        List<ClientService> clientServiceList = clientServiceDao.findByTag(AirTicketResource.Tag);
        for (ClientService clientService : clientServiceList) {
            try {
                Map<String, String> data = clientService.getMergeData();
                String startCity = data.get("start_city");
                String endCity = data.get("end_city");
                if (StringUtil.isEmpty(startCity) || StringUtil.isEmpty(endCity)) {
                    return;
                }

                List<AirTicket> result = getAirTickets(startCity, endCity);

                AirTicket minDiscountTicket = null;
                for (AirTicket airTicket : result) {
                    boolean isNewArticle = AirTicketStore.isNewAirTicket(airTicket);
                    if (isNewArticle) {
                        if (minDiscountTicket == null) {
                            minDiscountTicket = airTicket;
                        } else {
                            double currentDiscount = Double.valueOf(minDiscountTicket.getDiscount());
                            double discount = Double.valueOf(airTicket.getDiscount());
                            if (currentDiscount > discount) {
                                minDiscountTicket = airTicket;
                            }
                        }
                    }
                }

                if (minDiscountTicket != null) {
                    push(clientService, minDiscountTicket);
                }
                AirTicketStore.saveAirTicket(result);

                /*boolean isFlightNotInit = AirTicketStore.isFlightNotInit(startCity);
                if (isFlightNotInit && result.size() > 0) {
                    // 初始化数据并推送第一个航班信息
                    AirTicketStore.initAirData(result);
                    push(clientService, result.get(0));
                } else {
                    for (AirTicket airTicket : result) {
                        boolean isNewArticle = AirTicketStore.isNewAirTicket(airTicket);
                        if (isNewArticle) {
                            push(clientService, airTicket);
                            AirTicketStore.saveAirTicket(airTicket);
                        }
                    }
                }*/
                Thread.sleep(1000);
            } catch (Exception e) {
                // TODO 上报数据
                e.printStackTrace();
            }
        }

    }

    private void push(ClientService cs, AirTicket airTicket) throws HttpRequestException {
        String oneWayTemplate = "{start}-{end}有特价单程机票, {startData}起飞";
        String roundWayTemplate = "{start}-{end}有特价往返机票, {startData}起飞, {backData}回程";
        String title;
        if (airTicket.getType() == AirTicket.Type.ONE_WAY) {
            title = oneWayTemplate.replace("{start}", airTicket.getStart())
                    .replace("{end}", airTicket.getEnd())
                    .replace("{startData}", airTicket.getStartData());
        } else {
            title = roundWayTemplate.replace("{start}", airTicket.getStart())
                    .replace("{end}", airTicket.getEnd())
                    .replace("{startData}", airTicket.getStartData())
                    .replace("{backData}", airTicket.getBackData());
        }

        if (airTicket.getPrice() != 0) {
            title += ", 售价" + airTicket.getPrice() + "元起";
        }

        Article article = new Article();
        article.setTitle(title);
        article.setLink(airTicket.getLink());

        AlertMeClient.pushByCid(cs.getId()).setArticle(article).send();
    }

    private List<AirTicket> getAirTickets(String from, String to) {
        List<AirTicket> result = new ArrayList<>();
        try {
            String response = NativeHttpClient.get(REQUEST_URL
                    .replace("{start}", from)
                    .replace("{end}", to));
            Document doc = Jsoup.parse(response);
            Elements items = doc.getElementsByClass("item");
            for (Element item : items) {
                try {
                    String link = item.getElementsByTag("a").get(0).attr("href");
                    String paramStr = link.split("\\?")[1];
                    String[] params = paramStr.split("&");
                    Map<String, String> data = new HashMap<>();
                    data.put("link", BASE_URL + link);
                    for (String param : params) {
                        String[] kv = param.split("=");
                        if (kv.length == 2) {
                            data.put(kv[0], kv[1]);
                        }
                    }

                    if ("oneWay".equals(data.get("flightType"))) {
                        String price = getAirTicketPrice(from, to, data.get("startDate"));
                        if (!StringUtil.isEmpty(price)) {
                            data.put("price", price);
                        } else {
                            data.put("price", 0 + "");
                        }
                        data.put("discount", item.getElementsByClass("min_price").get(0).getElementsByClass("qn_font12").text().replace("折起", ""));
                        AirTicket airTicket = new AirTicket(data.get("startCity"), data.get("destCity"), data.get("startDate"), Integer.parseInt(data.get("price")), data.get("discount"), data.get("link"));
                        result.add(airTicket);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO 解析错误, 上报数据
                    // continue loop
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO 解析错误, 上报数据
        }

        return result;
    }

    private String getAirTicketPrice(String from, String to, String date) throws HttpRequestException {
        String fromCode = AirTicketStore.getAirportCode(from);
        String toCode = AirTicketStore.getAirportCode(to);
        if (StringUtil.isEmpty(fromCode) || StringUtil.isEmpty(toCode)) {
            return null;
        }
        String response = NativeHttpClient.get(PRICE_URL
                .replace("{start}", from)
                .replace("{end}", to)
                .replace("{fromCode}", fromCode)
                .replace("{toCode}", toCode)
                .replace("{date}", date));
        JsonObject json = JsonUtil.format(response, JsonObject.class);
        JsonObject data = json.get("data").getAsJsonObject();
        JsonArray list = data.get("list").getAsJsonArray();
        if (list != null && list.size() > 0) {
            JsonObject ticket = list.get(0).getAsJsonObject();
            String dc = ticket.get("dc").getAsString();
            String ac = ticket.get("ac").getAsString();
            String dd = ticket.get("dd").getAsString();
            String price = ticket.get("pr").getAsString();
            if (from.equals(dc) && to.equals(ac) && dd.equals(date)) {
                return price;
            }
        }
        return null;
    }

    public static void main(String[] args) throws HttpRequestException {
        //System.out.println(new AirSpiderJob().spiderAirTicketPrice("北京", "上海", "2015-01-21"));
        List<AirTicket> result = new AirSpiderJob().getAirTickets("北京", "上海");
        System.out.println(JsonUtil.toJson(result));
    }
}
