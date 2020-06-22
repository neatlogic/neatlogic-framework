package codedriver.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.exception.type.ParamIrregularException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-09 17:01
 **/
public class TimeUtil {

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME2_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String TIME3_FORMAT = "HH:mm";
    

    public static String timeTransfer(int timeRange, String timeUnit){
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        switch (timeUnit){
            case "year" :
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - timeRange);
                break;
            case "month" :
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - timeRange);
                break;
            case "week" :
                calendar.set(Calendar.WEEK_OF_YEAR,  calendar.get(Calendar.WEEK_OF_YEAR) - timeRange);
                break;
            case "day" :
                calendar.set(Calendar.DAY_OF_YEAR,  calendar.get(Calendar.DAY_OF_YEAR) - timeRange);
                break;
            case "hour" :
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - timeRange);
                break;
            case "minute" :
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - timeRange);
                break;
            case "second" :
                calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - timeRange);
                break;
            default:
        }
        return format.format(calendar.getTime());
    }

    public static String timeNow(){
        SimpleDateFormat format = new SimpleDateFormat(TIME2_FORMAT);
        return format.format(new Date());
    }
    
    public static Date getDateByHourMinute(String hourMinute) {
    	SimpleDateFormat  df = new SimpleDateFormat(TIME3_FORMAT);
    	SimpleDateFormat format = new SimpleDateFormat(TIME2_FORMAT);
    	try {
			if(StringUtils.isNotBlank(hourMinute)) {
				df.parse(hourMinute);
			}
			
		}catch(ParseException e) {
			throw new ParamIrregularException(hourMinute+"时间转换异常");
		}
    	Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR); 
        int month = calendar.get(Calendar.MONTH)+1;   
        int day = calendar.get(Calendar.DAY_OF_MONTH); 
        try {
			return format.parse(String.format("%d-%d-%d %s", year,month,day,hourMinute));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return null;
    }
}
