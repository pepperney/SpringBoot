package com.pepper.common.util;

import com.pepper.common.consts.Code;
import com.pepper.common.consts.Const;
import com.pepper.common.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class DateUtil extends DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    private static final long DAY_IN_MILLIS = 24 * 3600 * 1000;
    private static final long HOUR_IN_MILLIS = 3600 * 1000;
    private static final long MINUTE_IN_MILLIS = 60 * 1000;
    private static final long SECOND_IN_MILLIS = 1000;

    /**
     * Date转换为LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date转换为LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }


    /**
     * LocalDate转换为Date
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 返回两个日期之间的差值
     *
     * @param d1
     * @param d2
     * @param unit 时间单位，取值为ChronoUnit的枚举值：MILLIS|SECONDS|MINUTES|HOURS|DAYS|WEEKS|MONTHS|YEARS
     * @return
     */
    public static int dateDiff(Date d1, Date d2, ChronoUnit unit) {
        if (null == d1 || null == d2) {
            throw new CustomException(Code.ILLEGAL_PARAM, "日期不能为空");
        }
        LocalDateTime dt1 = dateToLocalDateTime(d1);
        LocalDateTime dt2 = dateToLocalDateTime(d2);
        long diffs = 0;
        switch (unit) {
            case MILLIS:
                diffs = ChronoUnit.MILLIS.between(dt1, dt2);
                break;
            case SECONDS:
                diffs = ChronoUnit.SECONDS.between(dt1, dt2);
                break;
            case MINUTES:
                diffs = ChronoUnit.MINUTES.between(dt1, dt2);
                break;
            case HOURS:
                diffs = ChronoUnit.HOURS.between(dt1, dt2);
                break;
            case DAYS:
                diffs = ChronoUnit.DAYS.between(dt1, dt2);
                break;
            case WEEKS:
                diffs = ChronoUnit.WEEKS.between(dt1, dt2);
                break;
            case MONTHS:
                diffs = ChronoUnit.MONTHS.between(dt1, dt2);
                break;
            case YEARS:
                diffs = ChronoUnit.YEARS.between(dt1, dt2);
                break;
        }
        return (int) diffs;
    }

    /**
     * 对指定日期date增加admout单位unit
     * unit的取值为ChronoUnit的枚举值：MILLIS|SECONDS|MINUTES|HOURS|DAYS|WEEKS|MONTHS|YEARS
     * amout的取值可为负数，为负数表示做减法
     *
     * @param date
     * @param amount
     * @param unit
     * @return
     */
    public static Date add(Date date, int amount, ChronoUnit unit) {
        if (null == date) {
            throw new CustomException(Code.ILLEGAL_PARAM, "日期不能为空");
        }
        LocalDateTime ldt = dateToLocalDateTime(date);
        LocalDateTime _ldt = null;
        _ldt = ldt.plus(amount, unit);
        return localDateTimeToDate(_ldt);
    }


    /**
     * 获取指定格式的日期字符串
     * @param date
     * @param pattern
     * @return
     */
    public static String getFormatDate(Date date,String pattern){
        if(null==date){
            return null;
        }
        LocalDateTime ldt = dateToLocalDateTime(date);
        return  ldt.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取格式为yyyy-MM-dd HH:mm:ss的日期字符串
     * @param date
     * @return
     */
    public static String getDefaultFormatDate(Date date){
        return getFormatDate(date,Const.DATETIME_PATTERN);
    }

    /**
     * 字符串日期转换为指定格式日期--不推荐使用
     * @param str
     * @param pattern
     * @return
     */
    public static Date strToDate(String str,String pattern){
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        try {
            return parseDate(str, pattern);
        } catch (ParseException e) {
            logger.error("日期转换出错",e);
            return null;
        }
    }

    /**
     * 获取时间戳--毫秒
     * @return
     */
    public static long getTimeMillis(){
        return System.currentTimeMillis();
    }

    /**
     * 获取时间戳--秒
     * @return
     */
    public static long getTimeSeconds(){
        return System.currentTimeMillis()/1000;
    }


    public static void main(String[] args) {
        Date dt = new Date();
        String str = getFormatDate(dt,"yyyy-MM-dd HH:mm:SS");
        System.out.println(str);
        System.out.println(strToDate(str,"yyyy-MM-dd HH:mm:SSS"));
    }

}