package com.sipsd.restful.api.downgoon.jresty.commons.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * REFER:
 * http://stackoverflow.com/questions/3083781/start-and-end-date-of-a-current
 * -month
 * */
public class DateUtil {

    /** 统计日的日期性质 */
    public static int getTypeValue(Date statDate) {
        Calendar calendar = new GregorianCalendar();
        int v = STAT_DATE_TYPE_EOD; // 默认普通日

        if (isEOW(calendar)) {
            v += STAT_DATE_TYPE_EOW;
        }
        if (isEOM(calendar)) {
            v += STAT_DATE_TYPE_EOM;

            if (isEOQ(calendar)) { // 季末以月末为前提
                v += STAT_DATE_TYPE_EOQ;

                if (isEOY(calendar)) { // 年末以季末为前提
                    v += STAT_DATE_TYPE_EOY;
                }
            }
        }

        return v;

    }

    /** 周末，严格说是周六，不是周日 */
    public static boolean isEOW(Date statDate) {
        return isEOW(date2calendar(statDate));
    }

    /** 周末（包括：周六和周日） */
    public static boolean isWeekend(Date statDate) {
        return isWeekend(date2calendar(statDate));
    }

    public static boolean isWeekend(Calendar calendar) {
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        /* 1表示周日；7表示周六 */
        return w == 1 || w == 7;
    }

    /**
     * @param isFromZero
     *            =true {0,1,2,...,6} 分别表示周日，周一，...，周六； =false {1,2,...,6,7}
     *            分别表示周日，周一，...，周六；
     * */
    public static int getDayOfWeek(Date date, boolean isFromZero) {
        int d = date2calendar(date).get(Calendar.DAY_OF_WEEK);
        return isFromZero ? (d - 1) : d;
    }

    /** 周六 */
    public static boolean isWeekendSat(Date statDate) {
        return isWeekendSat(date2calendar(statDate));
    }

    /** 周日 */
    public static boolean isWeekendSun(Date statDate) {
        return isWeekendSun(date2calendar(statDate));
    }

    /** 周六 */
    public static boolean isWeekendSat(Calendar calendar) {
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        return w == 7;
    }

    /** 周日 */
    public static boolean isWeekendSun(Calendar calendar) {
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        return w == 1;
    }

    public static boolean isEOW(Calendar calendar) {
        return calendar.getActualMaximum(Calendar.DAY_OF_WEEK) == calendar
                .get(Calendar.DAY_OF_WEEK);
    }

    /** 月末 */
    public static boolean isEOM(Date statDate) {
        return isEOM(date2calendar(statDate));
    }

    public static boolean isEOM(Calendar calendar) {
        // 如果当月的最后一天 等于当前天，则表示今天是月末
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH) == calendar
                .get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 季末，必定是月末 季度定义 ：3,6,9,12为季末
     * */
    public static boolean isEOQ(Date statDate) {
        return isEOQ(date2calendar(statDate));
    }

    public static boolean isEOQ(Calendar calendar) {
        boolean isEOM = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) == calendar
                .get(Calendar.DAY_OF_MONTH);
        return isEOM && (calendar.get(Calendar.MONTH) % 3 == 0);
    }

    /** 年末，必定是月末 */
    public static boolean isEOY(Date statDate) {
        return isEOY(date2calendar(statDate));

    }

    public static boolean isEOY(Calendar calendar) {
        boolean isEOM = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) == calendar
                .get(Calendar.DAY_OF_MONTH);
        return isEOM && (calendar.get(Calendar.MONTH) == 12);
    }

    private static Calendar date2calendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date todayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date yesterdayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date todayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date yesterdayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /** 日差（不足一天的舍去），例如：a=20120212,b=20120211,则返回1 */
    public static int diffDay(Date a, Date b) {
        long ta = a.getTime();
        long tb = b.getTime();
        return (int) ((ta - tb) / DAY1);
    }

    /** 日差（不足一天的舍去），例如：a=20120212,b=20120211,则返回1 */
    public static int diffDateDay(Date a, Date b) {
        Date dateA = trimHHmmssSSS(a);
        Date dateB = trimHHmmssSSS(b);
        return diffDay(dateA, dateB);
    }

    // REFER: http://sharajava.iteye.com/blog/81551 SimpleDateFormat 是非线程安全的
    // private static final SimpleDateFormat sdf = new
    // SimpleDateFormat("yyyy-MM-dd");
    // private static final SimpleDateFormat formater = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static int currentYYYYMMdd() {
        return Integer.parseInt(format(new Date(), "yyyyMMdd"));
    }

    public static int dateYYYYMMdd(Date date) {
        return Integer.parseInt(format(date, "yyyyMMdd"));
    }

    public static Date parse(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateFormat);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static Date parse(String dateString, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static Date parseDate(String dateString, String pattern)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateString);
    }

    public static String format(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(date);
    }

    public static String formatTime(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(date);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        return formater.format(date);
    }

    public static String format(long timestamp) {
        return format(timestamp, "yyyy/MM/dd HH:mm:ss");
    }

    public static String format(long timestamp, String pattern) {
        return format(new Date(timestamp), pattern);
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /** 把日期的HH:mm:ss SSS设置为0，只保留yyyyMMdd部分 */
    public static Date trimHHmmssSSS(Date date) {
        Calendar calendar = date2calendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /** 返回多少天前 */
    public static Date someDaysAgo(Date date, int days) {
        Calendar calendar = date2calendar(date);
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    public static final long MIN2 = 1000L * 60 * 2; // 2分钟
    public static final long MIN1 = 1000L * 60;
    public static final long DAY1 = 1000L * 60 * 60 * 24; // 1天

    /** 多少分钟前，并且去掉秒和毫秒 */
    public static Date someMinsAgo(long timestamp, int mins) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp - mins * 60 * 1000L));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    public static Date someMinsLater(long timestamp, int mins) {
        return someMinsAgo(timestamp, -mins);
    }

    /**
     * 多少天后
     * @param days
     *            可以为0；或负数。
     * */
    public static Date someDaysLater(Date date, int days) {
        Calendar calendar = date2calendar(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /** 统计日期居然早于初始化日期 */
    public static boolean beforeThan(Date statDate, Date initDate) {
        if (initDate == null) {
            return false;
        }
        return statDate.getTime() < initDate.getTime();
    }

    /** */
    public static boolean beforeEqualThan(Date statDate, Date initDate) {
        if (initDate == null) {
            return false;
        }
        return statDate.getTime() <= initDate.getTime();
    }

    public static Date min(Date a, Date b) {
        return a.getTime() > b.getTime() ? b : a;
    }

    public static Date max(Date a, Date b) {
        return a.getTime() > b.getTime() ? a : b;
    }

    /**
     * 产生起始日期到终止日期的连续日期，起始和终止都是闭区间。
     * */
    public static List<Date> dayByDayIncluded(Date startTime, Date endTime) {
        LinkedList<Date> dyd = new LinkedList<Date>();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(startTime);

        while (!calendar.getTime().after(endTime)) {
            dyd.add(calendar.getTime());
            calendar.add(GregorianCalendar.DAY_OF_YEAR, 1);
        }
        return dyd;
    }

    /** 获取当前时间(2011-10-25) */
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return format(calendar.getTime(), sdf.toPattern());
    }

    public static int daysAgo(byte timeSpanType) {
        int daysAgo = 0;
        switch (timeSpanType) {
        case TIMPSPAN_TYPE_WEEK:
            daysAgo = 7;
            break;
        case TIMPSPAN_TYPE_MONTH:
            daysAgo = 30;
            break;
        case TIMPSPAN_TYPE_QUARTER:
            daysAgo = 90;
            break;
        case TIMPSPAN_TYPE_HALFYEAR:
            daysAgo = 182;
            break;
        case TIMPSPAN_TYPE_YEAR:
            daysAgo = 365;
            break;
        case TIMPSPAN_TYPE_ALL:
        default:
            daysAgo = 0;
            break;
        }
        return daysAgo;
    }

    /**
     * 如果某一条既是周末，又是月末，同时还是季末，那么该数值等于：111，表达组合方式只需要累加就可以。
     * */
    public static final int STAT_DATE_TYPE_EOD = 0; // 普通
    public static final int STAT_DATE_TYPE_EOW = 001; // 周末 End Of Week
    public static final int STAT_DATE_TYPE_EOM = 010; // 月末 End Of Month
    public static final int STAT_DATE_TYPE_EOQ = 100; // 季末 End Of Quarter
                                                     // （必定是月末）
    public static final int STAT_DATE_TYPE_EOY = 1000; // 年末 End Of Year （必定是月末）

    public static final byte TIMPSPAN_TYPE_ALL = 0;
    public static final byte TIMPSPAN_TYPE_WEEK = 1;
    public static final byte TIMPSPAN_TYPE_MONTH = 2;
    public static final byte TIMPSPAN_TYPE_QUARTER = 3;
    public static final byte TIMPSPAN_TYPE_HALFYEAR = 4;
    public static final byte TIMPSPAN_TYPE_YEAR = 5;

    public static void main(String[] args) {
        // System.out.println(isEOM(parse("20111023","yyyyMMdd")));
        // System.out.println(isEOW(parse("20111023","yyyyMMdd")));
        // System.out.println(isEOW(parse("20111022","yyyyMMdd")));
        // System.out.println(format(someDaysAgo(parse("20111001","yyyyMMdd"),
        // 7)));

        System.out.println(isWeekend(parse("20121118", "yyyyMMdd")));
        System.out.println(isWeekend(parse("20121119", "yyyyMMdd")));
        System.out.println(isWeekend(parse("20121120", "yyyyMMdd")));
        System.out.println(isWeekend(parse("20121121", "yyyyMMdd")));
        System.out.println(isWeekend(parse("20121122", "yyyyMMdd")));
        System.out.println(isWeekend(parse("20121123", "yyyyMMdd")));
        System.out.println(isWeekend(parse("20121124", "yyyyMMdd")));
        System.out.println(isWeekend(parse("20121125", "yyyyMMdd")));

        System.out.println(diffDay(parse("20121118", "yyyyMMdd"),
                parse("20121118", "yyyyMMdd")));
        System.out.println(diffDay(parse("20121119", "yyyyMMdd"),
                parse("20121118", "yyyyMMdd")));

        System.out.println(dayByDayIncluded(parse("20121118", "yyyyMMdd"),
                parse("20121119", "yyyyMMdd")));


        System.out.println(someDaysLater(parse("20121118", "yyyyMMdd"), 0));

    }
}
