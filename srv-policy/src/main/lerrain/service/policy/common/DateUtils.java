package lerrain.service.policy.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class DateUtils {
    /**
     * 默认时间字符串的格式
     */
    public static final String                        LONG_DATE_PATTERN           = "yyyy-MM-dd HH:mm:ss";
    public static final String                        DEFAULT_FORMAT_STR          = "yyyy-MM-dd";
    public static final String                        SHORT_DATE_PATTERN_4_SEARCH = "yyyyMMdd";
    public static final String                        DEFAULT_X_FORMAT_STR        = "yyyy/MM/dd";

    public static final String                        YYYY_MM_DD_HH_MM_SS_SSS     = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String                        YYYY_MM_DD_HH_MM_SS         = "yyyy-MM-dd HH:mm:ss";
    public static final String                        ZH_TO_DAY                   = "yyyy年MM月dd日";
    //public static final SimpleDateFormat sfZhToDay                   = new SimpleDateFormat(ZH_TO_DAY);
    public static final ThreadLocal<SimpleDateFormat> sfZhToDay                   = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      public SimpleDateFormat get() {
                                                                                          return new SimpleDateFormat(
                                                                                                  ZH_TO_DAY);
                                                                                      }
                                                                                  };
    public static final String                        ZH_TO_MINUTE                = "yyyy年MM月dd日 HH时mm分";
    //public static final SimpleDateFormat              sfZhToMinute                = new SimpleDateFormat(ZH_TO_MINUTE);
    public static final ThreadLocal<SimpleDateFormat> sfZhToMinute                = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      public SimpleDateFormat get() {
                                                                                          return new SimpleDateFormat(
                                                                                                  ZH_TO_MINUTE);
                                                                                      }
                                                                                  };
    //public static final SimpleDateFormat              dateZhToDay                 = new SimpleDateFormat(
    //                                                                                      DEFAULT_FORMAT_STR);
    public static final ThreadLocal<SimpleDateFormat> dateZhToDay                 = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      public SimpleDateFormat get() {
                                                                                          return new SimpleDateFormat(
                                                                                                  DEFAULT_FORMAT_STR);
                                                                                      }
                                                                                  };
    public static final String                        ZH_TO_SECOND                = "yyyy年MM月dd日 HH时mm分ss秒";
    //public static final SimpleDateFormat              shZhToSecond                = new SimpleDateFormat(ZH_TO_SECOND);
    public static final ThreadLocal<SimpleDateFormat> shZhToSecond                = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      public SimpleDateFormat get() {
                                                                                          return new SimpleDateFormat(
                                                                                                  ZH_TO_SECOND);
                                                                                      }
                                                                                  };
    public static final String                        SHORT_SECOND                = "yyyyMMddHHmmss";

    /**
     * 把日期对象转成字符串，按yyyy年MM月dd日
     * 
     * @param date
     * @return
     */
    public static String zhToDay(Date date) {
        return date == null ? "" : sfZhToDay.get().format(date);
    }

    /**
     * 把格式为 yyyy-MM-dd的日期字符串 解析成日期类型
     * 
     * @param date
     * @return
     */
    public static Date dateToDay(String date) {
        Date result = null;
        try {
            if (StringUtils.isNotBlank(date)) {
                result = dateZhToDay.get().parse(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 把日期对象转成字符串，按yyyy年MM月dd日 HH时mm分
     * 
     * @param date
     * @return
     */
    public static String zhToMinute(Date date) {
        return date == null ? "" : sfZhToMinute.get().format(date);
    }

    /**
     * 计算当前时间离当天结束还有多少秒
     * 
     * @return
     */
    public static int calcTodayEndSecond() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        return (int) ((todayEnd.getTime().getTime() - (new Date().getTime())) / 1000);
    }

    public static String longFormat(Date date) {
        return format(date, LONG_DATE_PATTERN);
    }

    public static String shortFormat(Date date) {
        return format(date, DEFAULT_FORMAT_STR);
    }

    public static String shortFormat4Search(Date date) {
        return format(date, SHORT_DATE_PATTERN_4_SEARCH);
    }

    public static String format(Date date, String patern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat(patern);
        return sDateFormat.format(date);
    }

    /**
     * 获取当前时间
     * 
     * @param date
     * @return
     */
    public static String getCurrentDate(String formatStr) {
        if (null == formatStr) {
            formatStr = DEFAULT_FORMAT_STR;
        }
        return date2String(new Date(), formatStr);
    }

    /**
     * 将Date日期转换为String
     * 
     * @param date
     * @param formatStr
     * @return
     */
    public static String date2String(Date date, String formatStr) {
        if (null == date)
            return "";
        if (formatStr == null)
            formatStr = DEFAULT_FORMAT_STR;
        DateFormat df = createFormatter(formatStr);
        return df.format(date);
    }

    public static Date string2Date(String date) throws ParseException {
        DateFormat df = createFormatter(DEFAULT_FORMAT_STR);
        return df.parse(date);
    }

    public static Date string2Date(String date, String formatStr) throws ParseException {
        DateFormat df = createFormatter(formatStr);
        return df.parse(date);
    }

    public static Date string2LongDate(String date) throws ParseException {
        DateFormat df = createFormatter(LONG_DATE_PATTERN);
        return df.parse(date);
    }

    public static DateFormat createFormatter(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static String ymdFormat(Date date) {
        if (date == null) {
            return "";
        }
        DateFormat df = createFormatter(DEFAULT_FORMAT_STR);
        return df.format(date);
    }

    public static String ymdXFormat(Date date) {
        if (date == null) {
            return "";
        }
        DateFormat df = createFormatter(DEFAULT_X_FORMAT_STR);
        return df.format(date);
    }

    /**
     * 将String型格式化,比如想要将2011-11-11格式化成2011年11月11日,就StringPattern("2011-11-11",
     * "yyyy-MM-dd","yyyy年MM月dd日").
     * 
     * @param date String 想要格式化的日期
     * @param oldPattern String 想要格式化的日期的现有格式
     * @param newPattern String 想要格式化成什么格式
     * @return String
     */
    public static final String StringPattern(String date, String oldPattern, String newPattern) {
        if (date == null || oldPattern == null || newPattern == null)
            return "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldPattern); // 实例化模板对象    
        SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern); // 实例化模板对象    
        Date d = null;
        try {
            d = sdf1.parse(date); // 将给定的字符串中的日期提取出来    
        } catch (Exception e) { // 如果提供的字符串格式有错误，则进行异常处理    
            e.printStackTrace(); // 打印异常信息    
        }
        return sdf2.format(d);
    }

    /**
     * 把日期对象转成字符串，按yyyy年MM月dd日 HH时mm分ss秒
     * 
     * @param date
     * @return
     */
    public static String zhToSecond(Date date) {
        return date == null ? "" : shZhToSecond.get().format(date);
    }

    /**
     * 计算oneDate和otherDate 之间的分钟数
     * 
     * @param date
     * @return
     */
    public static Long calculateSeconds(Date oneDate, Date otherDate) {
        if (null == oneDate || null == otherDate) {
            return null;
        }
        try {
            return (oneDate.getTime() - otherDate.getTime()) / (1000);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算参数距离当前时间相差秒数
     * 
     * @param param 日期格式为yyyyMMddHHmmss
     * @return
     * @throws Exception
     */
    public static int calcParamCurSecond(String param) throws Exception {
        Date date = new SimpleDateFormat(SHORT_SECOND).parse(param);
        return (int) (new Date().getTime() - date.getTime()) / 1000;
    }

    /**
     * 获得日期字符串
     * 
     * @return yyyyMMddHHmmss
     */
    public static String shortSecondStr() {
        return new SimpleDateFormat(SHORT_SECOND).format(new Date());
    }

    /**
     * 获取在日期上增加一段时间后的日期
     * 
     * @author Mac
     * @param date1
     * @param calendarEx like:Calendar.DATE
     * @param time
     * @return
     */
    public static Date getDateAdd(Date date1, int calendarEx, int time) {
        Date endday = date1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endday);
        calendar.add(calendarEx, time);
        return calendar.getTime();

    }

    public static java.util.Date getStartOfDay(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setStartOfDay(calendar);
        return calendar.getTime();
    }

    public static java.util.Date getFirstDayOfMonth(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(5, 1);
        return calendar.getTime();
    }

    public static java.util.Date getFirstDayOfMonth() {
        return getFirstDayOfMonth(new java.util.Date());
    }

    public static void setStartOfDay(Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
    }

    public static java.util.Date getLastDayOfMonth(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(2, calendar.get(2) + 1);
        calendar.set(5, 1);
        calendar.add(5, -1);
        return calendar.getTime();
    }

    public static java.util.Date getEndOfDay(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setEndOfDay(calendar);
        return calendar.getTime();
    }

    public static void setEndOfDay(Calendar calendar) {
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
    }

    public static String getCurYearMonth() {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        return formatter.format(date);
    }

    public static String getCurYearMonth(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        return formatter.format(date);
    }

    public static String getYear(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        return formatter.format(date);
    }

    public static String getMonth(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        return formatter.format(date);
    }

}
