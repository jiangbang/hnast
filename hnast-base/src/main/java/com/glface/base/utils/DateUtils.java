package com.glface.base.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 日期工具类
 * @author maowei
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	private static String[] parsePatterns = { "yyyy-MM-dd",
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
			"yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" ,
			"yyyy-MM-dd'T'HH:mm:ss.SSS Z","yyyy/MM/dd'T'HH:mm:ss.SSS Z"};

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if(date==null){
			return formatDate;
		}
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		if (date == null) {
			return "";
		}
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null) {
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 *
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	public static Date getDateStart(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date getDateEnd(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		getFirstDayOfPreMonth();
		getLastDayOfPreMonth();
		getFirstDayOfCurrentMonth();
		getFirstDayOfCurrentWeek();
		compareDate("2015-11-02", "2015-11-03");
	}

	public static int compareDate(String DATE1, String DATE2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	public static boolean isSameYear(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		// subYear==0,说明是同一年
		if (subYear == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 得到上个月的第一天开始时间
	 */
	public static Date getFirstDayOfPreMonth() {
		Date nowdate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		/* 设置为当前时间 */
		cal.setTime(nowdate);
		/* 当前日期月份-1 */
		cal.add(Calendar.MONTH, -1);
		// 得到前一个月的第一天
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		System.out.println("上个月的第一天是：" + sdf.format(cal.getTime()));
		return cal.getTime();
	}

	/**
	 * 得到上个月的最后一天时间
	 */
	public static Date getLastDayOfPreMonth() {
		Date nowdate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		/* 设置为当前时间 */
		cal.setTime(nowdate);
		/* 当前日期月份-1 */
		cal.add(Calendar.MONTH, -1);
		// 得到前一个月的第一天
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		System.out.println("上个月的最后一天是：" + sdf.format(cal.getTime()));
		return getDateEnd(cal.getTime());
	}

	/**
	 * 得到现在这个月的第一天开始时间
	 */
	public static Date getFirstDayOfCurrentMonth() {
		Date nowdate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		/* 设置为当前时间 */
		cal.setTime(nowdate);
		/* 当前日期月份-1 */
		cal.add(Calendar.MONTH, 0);
		// 得到前一个月的第一天
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		System.out.println("这个月的第一天是：" + sdf.format(cal.getTime()));
		String sTime = sdf.format(cal.getTime());
		try {
			Date date = sdf.parse(sTime);
			return date;
		} catch (ParseException e) {
			return cal.getTime();
		}
	}

	public static Date getFirstDayOfCurrentWeek() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date nowdate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(nowdate);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		System.out.println("这周的第一天是：" + sdf.format(cal.getTime()));
		String sTime = sdf.format(cal.getTime());
		try {
			Date date = sdf.parse(sTime);
			return date;
		} catch (ParseException e) {
			return cal.getTime();
		}
	}

	/**
	 *
	 * 计算两个日期相差的月份数
	 *
	 * @param date1
	 *            日期1
	 * @param date2
	 *            日期2
	 * @param pattern
	 *            日期1和日期2的日期格式
	 * @return 相差的月份数
	 * @throws ParseException
	 */
	public static String countMonths(String date1, String date2, String pattern)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sdf.parse(date1));
		c2.setTime(sdf.parse(date2));

		Integer year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		Integer month;
		// 开始日期若小月结束日期
		if (year < 0) {
			year = -year;
			month = year * 12 + c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
		}
		month = year * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		System.out.println(month);
		return month / 12 + "年" + month % 12 + "月";
	}

	public static Integer countYears(String date1, String date2, String pattern)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sdf.parse(date1));
		c2.setTime(sdf.parse(date2));

		Integer year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		Integer month;
		// 开始日期若小月结束日期
		if (year < 0) {
			year = -year;
			month = year * 12 + c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
		}
		month = year * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);

		return month / 12;
	}

	/**
	 * 获取当年的第一天
	 *
	 * @return
	 */
	public static Date getCurrYearFirst() {
		Calendar currCal = Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		return getYearFirst(currentYear);
	}

	/**
	 * 获取某年第一天日期
	 *
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static Date getYearFirst(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	/**
	 * 得到某年某月的第一天
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getFirstDayOfMonth(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));

		return c.getTime();
	}

	/**
	 * 得到某年某月的最后一天
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getLastDayOfMonth(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

		return c.getTime();
	}
}
