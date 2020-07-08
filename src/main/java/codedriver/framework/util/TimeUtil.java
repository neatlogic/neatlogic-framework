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
    
    public static Date getDateByHourMinute(String hourMinute,Integer addDays) {
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
        int day = calendar.get(Calendar.DAY_OF_MONTH)+addDays; 
        try {
			return format.parse(String.format("%d-%d-%d %s", year,month,day,hourMinute));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    /**
	 * 比较执行时间窗口
	 * @param startTime
	 * @param endTime
	 * @return 0:在时间窗口之内，1:大于时间窗口，-1:小于时间窗口
	 */
	public static int isInTime(String startTimeStr,String endTimeStr) {
		if(StringUtils.isBlank(startTimeStr)&&StringUtils.isBlank(endTimeStr)) {//如果没有设置时间窗口
			return 0;
		}
		Date nowTime =null;
	    Date startTime = null;
	    Date endTime = null;
	    SimpleDateFormat  df = new SimpleDateFormat("H:mm");
		try {
			nowTime = df.parse(df.format(new Date()));
			startTime =df.parse(startTimeStr);
			endTime = df.parse(endTimeStr);
		}catch(ParseException e) {
			//时间格式不对，默认符合时间
			return 0;
		}
		Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return 0;
        }else if(date.after(end)){
        	return 1;
        }else{
            return -1;
        }
	}
}
