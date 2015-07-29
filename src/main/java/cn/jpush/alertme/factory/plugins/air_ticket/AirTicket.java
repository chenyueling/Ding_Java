package cn.jpush.alertme.factory.plugins.air_ticket;

import cn.jpush.alertme.factory.util.StringUtil;

/**
 * Created by ZeFanXie on 15-1-9.
 */
public class AirTicket {
    private String id;
    private String start;
    private String end;
    private Type type;
    private String startData;
    private String backData;
    private int price;
    private String discount;
    private String link;

    public AirTicket() {
    }

    /**
     * 单程机票
     * @param start
     * @param end
     * @param startData
     * @param price
     * @param discount
     */
    public AirTicket(String start, String end, String startData, int price, String discount, String link) {
        this.start = start;
        this.end = end;
        this.startData = startData;
        this.price = price;
        this.discount = discount;
        this.link = link;
        this.type = Type.ONE_WAY;
        this.id = buildId();
    }

    /**
     * 往返机票
     * @param start
     * @param end
     * @param startData
     * @param backData
     * @param price
     * @param discount
     */
    public AirTicket(String start, String end, String startData, String backData, int price, String discount, String link) {
        this.start = start;
        this.end = end;
        this.startData = startData;
        this.backData = backData;
        this.price = price;
        this.discount = discount;
        this.link = link;
        this.type = Type.ROUND_WAY;
        this.id = buildId();
    }

    private String buildId() {
        return StringUtil.toMD5(start + ":" + end + ":" + type.toString() + ":" + startData + ":" + (type == Type.ROUND_WAY ? backData + ":" : ""));
    }

    public boolean validate() {
        return !StringUtil.isEmpty(start)
                && !StringUtil.isEmpty(end)
                && !StringUtil.isEmpty(startData)
                && (type == Type.ONE_WAY || !StringUtil.isEmpty(backData))
                && price != 0
                && !StringUtil.isEmpty(discount);
    }


    public static enum Type {
        ONE_WAY, ROUND_WAY
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getStartData() {
        return startData;
    }

    public void setStartData(String startData) {
        this.startData = startData;
    }

    public String getBackData() {
        return backData;
    }

    public void setBackData(String backData) {
        this.backData = backData;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
