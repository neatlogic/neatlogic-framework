package codedriver.framework.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-09 17:01
 **/
public class TimeUtil {

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String timeTransfer(int timeRange, String timeUnit){
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        switch (timeUnit){
            case "year" :
                calendar.set(Calendar.YEAR, -timeRange);
            case "month" :
                calendar.set(Calendar.MONTH, -timeRange);
            case "week" :
                calendar.set(Calendar.WEEK_OF_YEAR, -timeRange);
            case "day" :
                calendar.set(Calendar.DAY_OF_MONTH, -timeRange);
            case "hour" :
                calendar.set(Calendar.HOUR_OF_DAY, -timeRange);
            case "minute" :
                calendar.set(Calendar.MINUTE, -timeRange);
            case "second" :
                calendar.set(Calendar.SECOND, -timeRange);
            default: ;
        }
        return format.format(calendar.getTime());
    }

    public static void main(String[] args) {
        System.out.println(timeTransfer(1,"hour"));
    }
}
