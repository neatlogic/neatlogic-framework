package neatlogic.framework.util;

import neatlogic.framework.exception.type.ParamIrregularException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;
import static org.apache.commons.lang3.time.DateUtils.toCalendar;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-03-09 17:01
 **/
public class TimeUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYYMMDD_HHMMSS = "yyyyMMdd-HHmmss";
    public static final String FMT_yyyy_MM_dd = "yyyy-MM-dd";
    private static final String yyyyMMdd = "yyyyMMdd";
    private static final String yyMMdd = "yyMMdd";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public static final String HH_MM = "HH:mm";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    public static String timeTransfer(int timeRange, String timeUnit) {
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        switch (timeUnit) {
            case "year":
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - timeRange);
                break;
            case "month":
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - timeRange);
                break;
            case "week":
                int week = calendar.get(Calendar.WEEK_OF_YEAR);
                int mouth = calendar.get(Calendar.MONTH);
                //???????????????12????????????????????????????????????????????????????????????????????????????????????53?????????????????????????????????
                if (mouth >= 11 && week <= 1) {
                    week += 52;
                }
                calendar.set(Calendar.WEEK_OF_YEAR, week - timeRange);
                break;
            case "day":
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - timeRange);
                break;
            case "hour":
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - timeRange);
                break;
            case "minute":
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - timeRange);
                break;
            case "second":
                calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - timeRange);
                break;
            default:
        }
        return format.format(calendar.getTime());
    }

    public static Date recentTimeTransfer(int timeRange, String timeUnit) {
        Calendar calendar = Calendar.getInstance();
        switch (timeUnit) {
            case "year":
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - timeRange);
                break;
            case "month":
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - timeRange);
                break;
            case "week":
                calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) - timeRange);
                break;
            case "day":
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - timeRange);
                break;
            case "hour":
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - timeRange);
                break;
            case "minute":
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - timeRange);
                break;
            case "second":
                calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - timeRange);
                break;
            default:
        }
        return calendar.getTime();
    }

    public static String timeNow() {
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return format.format(new Date());
    }

    public static Date getDateByHourMinute(String hourMinute, Integer addDays) {
        SimpleDateFormat df = new SimpleDateFormat(HH_MM);
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
        try {
            if (StringUtils.isNotBlank(hourMinute)) {
                df.parse(hourMinute);
            }

        } catch (ParseException e) {
            throw new ParamIrregularException(hourMinute);
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH) + addDays;
        try {
            return format.parse(String.format("%d-%d-%d %s", year, month, day, hourMinute));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ????????????????????????
     *
     * @param startTimeStr ????????????
     * @param endTimeStr   ????????????
     * @return 0:????????????????????????1:??????????????????(????????????)???-1:??????????????????
     */
    public static int isInTimeWindow(String startTimeStr, String endTimeStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HH_MM);
        String currentTime = simpleDateFormat.format(new Date());
        if (StringUtils.isNotBlank(startTimeStr) && StringUtils.isNotBlank(endTimeStr)) {
            if (startTimeStr.compareTo(endTimeStr) <= 0) {//??????????????????????????????(???????????????)
                if (currentTime.compareTo(startTimeStr) < 0) {
                    /* ?????????????????????????????? */
                    return -1;
                } else if (currentTime.compareTo(startTimeStr) >= 0 && currentTime.compareTo(endTimeStr) <= 0) {
                    /* ???????????????????????????????????????????????????*/
                    return 0;
                } else {
                    /* ?????????????????????????????? */
                    return 1;
                }
            } else {//??????????????????????????????(??????)
                if (currentTime.compareTo(startTimeStr) >= 0 || currentTime.compareTo(endTimeStr) <= 0) {
                    return 0;
                } else {
                    return -1;
                }
            }
        } else if (StringUtils.isNotBlank(startTimeStr)) {//??????????????????
            if (currentTime.compareTo(startTimeStr) < 0) {
                return -1;
            } else {
                return 0;
            }
        } else if (StringUtils.isNotBlank(endTimeStr)) {//??????????????????
            if (currentTime.compareTo(endTimeStr) > 0) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static String addDateByDay(Date date, int day, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (int i = 0; i < day; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return TimeUtil.convertDateToString(calendar.getTime(), format);
    }

    public static String addDateByWorkDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (int i = 0; i < day; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (checkHoliday(calendar)) {
                i--;
            }
        }
        return TimeUtil.convertDateToString(calendar.getTime(), TimeUtil.FMT_yyyy_MM_dd);
    }

    public static boolean checkHoliday(Calendar calendar) {
        //?????????????????????????????????
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return true;
        }
        return false;
    }

    public static String convertDateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date convertStringToDate(String dataStr, String format) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(dataStr);
        } catch (Exception ex) {
            return null;
        }
        return date;
    }

    public static Date formatDate(Date date, String format) throws ParseException {
        return convertStringToDate(convertDateToString(date, format), format);
    }


    /**
     * ?????????????????????????????????
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getWorkingDay(Date startDate, Date endDate) {
        int workDay = 0;
        Calendar start = toCalendar(startDate);
        Calendar end = toCalendar(endDate);
        while (true) {
            if (checkHoliday(start)) {
                start.add(Calendar.DATE, 1);
                continue;
            }
            if (start.before(end) && !isSameDay(start, end)) {
                workDay++;
                start.add(Calendar.DATE, 1);
                continue;
            }
            return workDay;
        }
    }

    /**
     * ?????????????????????, e.g.: 2019-01-01 00:00:00
     */
    public static Date firstDayOfMonth(Date now) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertStringToDate(convertDateToString(now, FMT_yyyy_MM_dd), FMT_yyyy_MM_dd));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * ????????????????????????, e.g.: 2019-01-31 23:59:59
     */
    public static Date lastDayOfMonth(Date last) throws ParseException {
        Calendar ca = Calendar.getInstance();
        ca.setTime(last);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        Calendar calen = Calendar.getInstance();
        calen.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        Date monthLastDate = calen.getTime();
        return monthLastDate;
    }

    /**
     * ?????????????????????????????????
     *
     * @param startTime yyyy-MM
     * @param endTime   yyyy-MM
     * @return
     */
    public static List<String> calculateMonthly(String startTime, String endTime) throws ParseException {
        List<String> result = new ArrayList<>();
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            Date endDate = dateFormat.parse(startTime);
            Date endDate2 = dateFormat.parse(endTime);
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(endDate);
            c2.setTime(endDate2);
            int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
            if (year < 0) {
//            year = -year;
//            int num = year * 12 + c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
            } else {
                if (year == 0) {
                    int num2 = year * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
                    for (int i = 1; i <= num2 + 1; i++) {
                        int mon = c1.get(Calendar.MONDAY) + i;
                        String value;
                        if (mon < 10) {
                            value = c1.get(Calendar.YEAR) + "-0" + mon;
                        } else {
                            value = c1.get(Calendar.YEAR) + "-" + mon;
                        }
//                        String value = c1.get(Calendar.YEAR) + "-" + (c1.get(Calendar.MONDAY) + i);
                        result.add(value);
                    }
                } else {
                    int y = c1.get(Calendar.YEAR);
                    int y2 = c2.get(Calendar.YEAR);
                    int m = c1.get(Calendar.MONDAY);
                    int num = 12;
                    for (int i = 0; i <= year; i++) {
                        if (y == y2) {
                            num = (c2.get(Calendar.MONDAY) + 1);
                        }
                        for (int k = 1; m + k <= num; k++) {
                            int mon = m + k;
                            String value;
                            if (mon < 10) {
                                value = y + "-0" + mon;
                            } else {
                                value = y + "-" + mon;
                            }
//                            String value = y + "-" + (m + k);
                            result.add(value);
                        }
                        y += 1;
                        m = 0;
                    }
                }
            }
        }
        return result;
    }

    /**
     * ????????????
     */
    public static Boolean compareDate(Date date, Date compare) {
        if (date == null && compare == null) {
            return true;
        }
        if (date != null && compare == null) {
            return true;
        }
        if (date == null && compare != null) {
            return false;
        }
        return date.getTime() >= compare.getTime();
    }

    /**
     * ?????????????????????
     *
     * @param timeFormat
     * @return
     */
    public static String getYesterdayByFormat(String timeFormat) {
        return LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern(timeFormat));
    }

    /**
     * ??????????????????????????????
     *
     * @param timeFormat
     * @return
     */
    public static String getDateString(String timeFormat) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat));
    }

    /**
     * ?????????n?????????????????????
     *
     * @param date
     * @param days
     * @return
     */
    public static String getMinusDay(String date, int days) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parse = LocalDate.parse(date, dtf);
        return parse.minusDays(days).toString();
    }

    /**
     * ??????????????????????????????
     *
     * @param date
     * @return
     */
    public static Date getPreDateByDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_yyyy_MM_dd);
        String preDateStr = getPreDateByDate(sdf.format(date));
        Date preDate;
        try {
            preDate = sdf.parse(preDateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return preDate;
    }

    /**
     * ??????????????????????????????
     *
     * @param strData
     * @return
     */
    public static String getPreDateByDate(String strData) {
        String preDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_yyyy_MM_dd);
        Date date = null;
        try {
            date = sdf.parse(strData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day1 = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day1 - 1);
        preDate = sdf.format(c.getTime());
        return preDate;
    }

    /**
     * ??????????????????
     *
     * @param date
     * @return
     */
    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // ?????????????????????????????????????????????
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // ???????????????????????????????????????????????????????????????????????????????????????
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // ?????????????????????????????????????????????
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // ???????????????????????????????????????????????????????????????????????????????????????
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    /**
     * ??????????????????
     *
     * @param date
     * @return
     */
    public static Date getThisWeekSunday(Date date) {
        Date monday = getThisWeekMonday(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(monday);
        cal.add(Calendar.DATE, 6);
        return cal.getTime();
    }

    /**
     * ?????????????????????, e.g.: 2019-01-01 00:00:00
     */
    public static Date firstDayOfYear(Date now) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        Calendar cal = Calendar.getInstance();
        cal.set(calendar.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        return cal.getTime();
    }

    /**
     * ????????????????????????, e.g.: 2019-12-31 23:59:59
     */
    public static Date lastDayOfYear(Date now) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        Calendar cal = Calendar.getInstance();
        cal.set(calendar.get(Calendar.YEAR), 11, 31, 23, 59, 59);
        return cal.getTime();
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param startDay ????????????
     * @param endDay   ????????????
     * @return
     * @throws ParseException
     */
    @SuppressWarnings("unused")
    public static List<Date> calBetweenDaysDate(String startDay, String endDay) throws ParseException {
        List<Date> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_yyyy_MM_dd);
        Calendar ca = Calendar.getInstance();
        ca.setTime(sdf.parse(startDay));
        long t1 = ca.getTime().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(endDay));
        long t2 = cal.getTime().getTime();
        for (int i = 0; t1 <= t2; i++) {
            result.add(ca.getTime());
            ca.add(Calendar.DATE, 1);
            t1 = ca.getTime().getTime();
        }
        return result;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param date
     * @return
     */
    @SuppressWarnings("unused")
    public static List<String> calFirstDay2Date(Date date) throws ParseException {
        List<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_yyyy_MM_dd);
        Date firstDay = firstDayOfMonth(date);
        Calendar ca = Calendar.getInstance();
        ca.setTime(firstDay);
        long t1 = ca.getTime().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(formatDate(date, FMT_yyyy_MM_dd));
        long t2 = cal.getTime().getTime();
        for (int i = 0; t1 <= t2; i++) {
            result.add(sdf.format(ca.getTime()));
            ca.add(Calendar.DATE, 1);
            t1 = ca.getTime().getTime();
        }
        return result;
    }

    /**
     * ?????????????????????????????????
     *
     * @param date
     * @return
     */
    @SuppressWarnings("unused")
    public static List<String> calMonday2Date(Date date) {
        List<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_yyyy_MM_dd);
        Date monday = getThisWeekMonday(date);
        Calendar ca = Calendar.getInstance();
        ca.setTime(monday);
        long t1 = ca.getTime().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        long t2 = cal.getTime().getTime();
        for (int i = 0; t1 <= t2; i++) {
            result.add(sdf.format(ca.getTime()));
            ca.add(Calendar.DATE, 1);
            t1 = ca.getTime().getTime();
        }
        return result;
    }

    /**
     * ?????????????????????????????????
     *
     * @param date
     * @return
     */
    public static int getThisWeekNum(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.YEAR, ca.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, ca.get(Calendar.MONTH));
        cal.set(Calendar.DATE, ca.get(Calendar.DATE));
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * ?????????????????????????????????
     *
     * @param month yyyy-MM
     * @return
     */
    public static String getLastYearMonth(String month) {
        String lastYearMonth = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = sdf.parse(month);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, -1);
        lastYearMonth = sdf.format(c.getTime());
        return lastYearMonth;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public static Date clearDateTail(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        // ????????????,???????????????
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    public static Date fullDate(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        // ????????????,???????????????
        cal1.set(Calendar.HOUR_OF_DAY, 23);
        cal1.set(Calendar.MINUTE, 59);
        cal1.set(Calendar.SECOND, 59);
        cal1.set(Calendar.MILLISECOND, 999);
        return cal1.getTime();
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public static String firstDayOfWeekStr() {
        return LocalDate.now().with(DayOfWeek.MONDAY).toString() + " 00:00:00";
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public static String lastDayOfWeekStr() {
        return LocalDate.now().with(DayOfWeek.SUNDAY).toString() + " 23:59:59";
    }

    /**
     * ?????????
     * ??????????????????  yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String firstDayOfMonthStr() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString() + " 00:00:00";
    }

    /**
     * ???????????????????????????  yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String lastDayOfMonthStr() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString() + " 23:59:59";
    }

    /**
     * ????????????????????????  yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String firstDayOfLastMonth() {
        return LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).toString() + " 00:00:00";
    }

    /**
     * ???????????????????????????  yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String lastDayOfLastMonth() {
        return LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).toString() + " 23:59:59";
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public static String firstDayOfLastYearMonth() {
        return LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfMonth()).toString() + " 00:00:00";
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    public static String lastDayOfLastYearMonth() {
        return LocalDate.now().minusYears(1).with(TemporalAdjusters.lastDayOfMonth()).toString() + " 23:59:59";
    }

    public static String yyyymmdd() {
        return DateTimeFormatter.ofPattern(yyyyMMdd).format(LocalDate.now());
    }

    public static String yymmdd() {
        return DateTimeFormatter.ofPattern(yyMMdd).format(LocalDate.now());
    }

    public static String yyyyMMddHHmmss() {
        return DateTimeFormatter.ofPattern(yyyyMMddHHmmss).format(LocalDateTime.now());
    }

    /**
     * ?????????????????????
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     */
    @SuppressWarnings("unused")
    public static boolean checkTimeParam(String startTime, String endTime, String format) {
        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            return false;
        }

        if (startTime.compareTo(endTime) > 0) {
            return false;
        }

        Date _startTime = convertStringToDate(startTime, format);
        Date _endTime = convertStringToDate(endTime, format);
        return _startTime != null && _endTime != null;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static String differentStrByDate(Date startDate, Date endDate) {
        long diffTime = endDate.getTime() - startDate.getTime();

        long nd = 1000L * 24 * 60 * 60;
        long nh = 1000L * 60 * 60;
        long nm = 1000L * 60;
        long ns = 1000L;

        // ??????????????????
        long day = diffTime / nd;
        // ?????????????????????
        long hour = diffTime % nd / nh;
        // ?????????????????????
        long min = diffTime % nd % nh / nm;
        // ??????????????????
        long sec = diffTime % nd % nh % nm / ns;
        return day + "???" + hour + "??????" + min + "??????" + sec + "???";
    }

    /**
     * @param milliseconds ?????????
     * @return String
     * @description : ?????????????????????????????????????????????
     * @since 2020???9???14???
     */
    public static String millisecondsTransferMaxTimeUnit(long milliseconds) {
        if (milliseconds >= 24 * 60 * 60 * 1000) {
            return (milliseconds / (24 * 60 * 60 * 1000)) + " ???";
        } else if (milliseconds >= (60 * 60 * 1000)) {
            return (milliseconds / (60 * 60 * 1000)) + " ??????";
        } else if (milliseconds >= 60 * 1000) {
            return (milliseconds / (60 * 1000)) + " ??????";
        } else if (milliseconds >= 1000) {
            return (milliseconds / 1000) + " ???";
        } else {
            return milliseconds + " ??????";
        }
    }

    /**
     * @param dateConfig ??????date????????????
     * @return ??????????????????starttime ??? endtime
     * @author lvzk
     * @description ????????????date?????????????????????startTime ??? endTime
     * @since 2020???11???6???
     */
    public static JSONObject getStartTimeAndEndTimeByDateJson(JSONObject dateConfig) {
        JSONObject json = new JSONObject();
        String startTime;
        String endTime;
        SimpleDateFormat format = new SimpleDateFormat(TimeUtil.YYYY_MM_DD_HH_MM_SS);
        if (dateConfig.containsKey("startTime")) {
            startTime = format.format(new Date(dateConfig.getLong("startTime")));
            endTime = format.format(new Date(dateConfig.getLong("endTime")));
        } else {
            startTime = TimeUtil.timeTransfer(dateConfig.getInteger("timeRange"), dateConfig.getString("timeUnit"));
            endTime = TimeUtil.timeNow();
        }
        json.put("startTime", startTime);
        json.put("endTime", endTime);

        return json;
    }

    public static List<Long> getTimeRangeList(JSONObject timeRangeObj) {
        long startTime = 0L;
        long endTime = 0L;
        if (MapUtils.isNotEmpty(timeRangeObj)) {
            String unit = timeRangeObj.getString("timeUnit");
            String timeRange = timeRangeObj.getString("timeRange");
            long st = timeRangeObj.getLongValue("startTime");
            long et = timeRangeObj.getLongValue("endTime");
            if (StringUtils.isNotBlank(unit) && StringUtils.isNotBlank(timeRange)) {
                endTime = System.currentTimeMillis() / 1000;
                int tr = Integer.parseInt(timeRange);
                Calendar now = Calendar.getInstance();
                switch (unit) {
                    case "day":
                        now.add(Calendar.DAY_OF_YEAR, -tr);
                        break;
                    case "week":
                        now.add(Calendar.WEEK_OF_YEAR, -tr);
                        break;
                    case "month":
                        now.add(Calendar.MONTH, -tr);
                        break;
                    case "year":
                        now.add(Calendar.YEAR, -tr);
                        break;
                }
                startTime = now.getTimeInMillis() / 1000;
            } else if (st > 0 && et > 0) {
                startTime = st / 1000;
                endTime = et / 1000;
            }
        }
        List<Long> timeRangeList = new ArrayList<>();
        if (startTime > 0 && endTime > 0 && startTime <= endTime) {
            timeRangeList.add(startTime);
            timeRangeList.add(endTime);
        }
        return timeRangeList;
    }

    /**
     * ??????????????????????????????unitCount???minTimeUnit?????????????????????????????????????????????????????????
     * ????????????milliseconds = 266646000???unitCount=4???minTimeUnit=TimeUnit.SECONDS???separator=" "??? ????????????3??? 2?????? 4?????? 6???
     * ????????????milliseconds = 266646000???unitCount=3???minTimeUnit=TimeUnit.SECONDS???separator=" "??? ????????????3??? 2?????? 4??????
     * ????????????milliseconds = 266646000???unitCount=2???minTimeUnit=TimeUnit.SECONDS???separator=" "??? ????????????3??? 2??????
     * ????????????milliseconds = 266646000???unitCount=1???minTimeUnit=TimeUnit.SECONDS???separator=" "??? ????????????3???
     * ????????????milliseconds = 266646000???unitCount=4???minTimeUnit=TimeUnit.DAYS???separator=" "??? ????????????3???
     * ????????????milliseconds = 266646000???unitCount=4???minTimeUnit=TimeUnit.HOURS???separator=" "??? ????????????3??? 2??????
     * ????????????milliseconds = 266646000???unitCount=4???minTimeUnit=TimeUnit.MINUTES???separator=" "??? ????????????3??? 2?????? 4??????
     * ????????????milliseconds = 266646000???unitCount=4???minTimeUnit=TimeUnit.SECONDS???separator="-"??? ????????????3???-2??????-4??????-6???
     * @param milliseconds ?????????
     * @param unitCount ??????????????????????????????
     * @param minTimeUnit ????????????????????????
     * @param separator ??????????????????????????????
     * @return
     */
    public static String millisecondsFormat(Long milliseconds, int unitCount, TimeUnit minTimeUnit, String separator) {
        List<String> list = new ArrayList<>(unitCount);
        if (milliseconds >= TimeUnit.DAYS.toMillis(1)) {
            list.add((milliseconds / TimeUnit.DAYS.toMillis(1)) + I18nUtils.getMessage("const.days"));
            if (list.size() >= unitCount) {
                return String.join(separator, list);
            }
            if (TimeUnit.DAYS == minTimeUnit) {
                return String.join(separator, list);
            }
            milliseconds = milliseconds % TimeUnit.DAYS.toMillis(1);
        }
        if (milliseconds >= TimeUnit.HOURS.toMillis(1)) {
            list.add((milliseconds / TimeUnit.HOURS.toMillis(1)) + I18nUtils.getMessage("const.hours"));
            if (list.size() >= unitCount) {
                return String.join(separator, list);
            }
            if (TimeUnit.HOURS == minTimeUnit) {
                return String.join(separator, list);
            }
            milliseconds = milliseconds % TimeUnit.HOURS.toMillis(1);
        }
        if (milliseconds >= TimeUnit.MINUTES.toMillis(1)) {
            list.add((milliseconds / TimeUnit.MINUTES.toMillis(1)) + I18nUtils.getMessage("const.minutes"));
            if (list.size() >= unitCount) {
                return String.join(separator, list);
            }
            if (TimeUnit.MINUTES == minTimeUnit) {
                return String.join(separator, list);
            }
            milliseconds = milliseconds % TimeUnit.MINUTES.toMillis(1);
        }
        if (milliseconds >= TimeUnit.SECONDS.toMillis(1)) {
            list.add((milliseconds / TimeUnit.SECONDS.toMillis(1)) + I18nUtils.getMessage("const.seconds"));
        }
        return String.join(separator, list);
    }
}
