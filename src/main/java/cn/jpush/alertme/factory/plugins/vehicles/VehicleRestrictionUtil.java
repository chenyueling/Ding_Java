package cn.jpush.alertme.factory.plugins.vehicles;

import cn.jpush.alertme.factory.util.JsonUtil;
import cn.jpush.alertme.factory.util.LegalFestivalsUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/29.
 */
public class VehicleRestrictionUtil {
    private static final String url = "";


    private static String week[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    private static final String json = "{\n" +
            "    \"Monday\": [\"2\", \"7\"],\n" +
            "    \"Tuesday\": [\"3\", \"8\"],\n" +
            "    \"Wednesday\": [\"4\", \"9\"],\n" +
            "    \"Thursday\": [\"5\", \"0\",\"字母\"],\n" +
            "    \"Friday\": [\"1\", \"6\"],\n" +
            "    \"Saturday\": [],\n" +
            "    \"Sunday\": []\n" +
            "}";

    /**
     * today is vehicle limit
     *
     * @param num
     * @return
     */
    public static boolean isLimited(String num) throws IOException {

        Map<String, List<String>> map = JsonUtil.jsonToMap(json);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, 1);

        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);

        List<String> list = map.get(week[day_of_week - 1]);

        if(LegalFestivalsUtils.isLegalFestivalTommrow()){
            return false;
        }

        for (String s : list) {
            if (num.equals(s)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 10; i++) {
            System.out.println(isLimited(i+""));
        }
        System.out.println(isLimited("字母"));
    }
}
