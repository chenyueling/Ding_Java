package cn.jpush.alertme.factory.util;

import java.util.Date;

/**
 * Date Common Method
 * Created by ZeFanXie on 14-12-29.
 */
public class DateUtil {
    public static final long ONE_DATE = 86400000;

    /**
     * 计算日期A与日期B是否在一个时间间隔(within)之内
     * @param dateA
     * @param dateB
     * @param within
     * @return
     */
    public static boolean within(Date dateA, Date dateB, long within) {
        return Math.abs(dateA.getTime() - dateB.getTime()) < within;
    }

}
