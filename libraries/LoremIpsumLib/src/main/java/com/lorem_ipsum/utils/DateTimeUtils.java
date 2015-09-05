package com.lorem_ipsum.utils;

import android.app.Activity;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by DangTai on [18 March 2014].
 */
public final class DateTimeUtils {

	private static SimpleDateFormat fullDateFormatter, halfDateFormatter;

    public static String formatDateTimeWithZone(Date date) {
        if (date == null)
            return null;
        if (fullDateFormatter == null) {
            fullDateFormatter = new SimpleDateFormat("d MMM yyyy, hh:mma z", java.util.Locale.getDefault());
            fullDateFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        }
        return fullDateFormatter.format(date);
    }

    public static String formatDateTimeWithoutZone(Date date) {
        if (date == null)
            return null;
        if (halfDateFormatter == null) {
            halfDateFormatter = new SimpleDateFormat("E, d MMM yyyy, hh:mma", java.util.Locale.getDefault());
            halfDateFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        }
        return halfDateFormatter.format(date);
    }

    public static String formatDateWithoutZone(Date date) {
        if (date == null)
            return null;
        if (halfDateFormatter == null) {
            halfDateFormatter = new SimpleDateFormat("EEE, d MMM ''yy", java.util.Locale.getDefault());
            halfDateFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        }
        return halfDateFormatter.format(date);
    }

    public static String formatDateWithFormatString(Date date, String format) {
        if (date == null)
            return null;

        SimpleDateFormat formatter = null;
        try {
            formatter = new SimpleDateFormat(format);
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        }
        catch (Exception e) {
            formatter = null;
        }
        if (formatter == null)
            return null;

        return formatter.format(date);
    }

    public static String getDayNumberSuffix(final int n) {
        if (n <= 1 || n > 31)
            return "";
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

	public static String formatRemainingDateTime(long inputDate) {
		long distanceMili = inputDate - System.currentTimeMillis();
		if ( distanceMili <= 0) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		if (distanceMili >= Constants.TIME_ONE_YEAR) {
			int years = (int) (distanceMili / Constants.TIME_ONE_YEAR);
			builder.append(years);
			if (years == 1) {
				builder.append(" year");
			} else {
				builder.append(" years");
			}

		} else if (distanceMili >= Constants.TIME_ONE_MONTH) {
			int months = (int) (distanceMili / Constants.TIME_ONE_MONTH);
			builder.append(months);
			if (months == 1) {
				builder.append(" month");
			} else {
				builder.append(" months");
			}

		} else if (distanceMili >= Constants.TIME_ONE_WEEK) {
			int weeks = (int) (distanceMili / Constants.TIME_ONE_WEEK);
			builder.append(weeks);
			if (weeks == 1) {
				builder.append(" week");
			} else {
				builder.append(" weeks");
			}

		} else if (distanceMili >= Constants.TIME_ONE_DAY) {
			int days = (int) (distanceMili / Constants.TIME_ONE_DAY);
			builder.append(days);
			if (days == 1) {
				builder.append(" day");
			} else {
				builder.append(" days");
			}

		} else if (distanceMili >= Constants.TIME_ONE_HOUR) {
			int hours = (int) (distanceMili / Constants.TIME_ONE_HOUR);
			builder.append(hours);
			if (hours == 1) {
				builder.append(" hour");
			} else {
				builder.append(" hours");
			}

		} else if (distanceMili >= Constants.TIME_ONE_MINUTE) {
			int minutes = (int) (distanceMili / Constants.TIME_ONE_MINUTE);
			builder.append(minutes);
			if (minutes == 1) {
				builder.append(" minute");
			} else {
				builder.append(" minutes");
			}

		} else {
			builder.append("1 minute");
		}

		builder.append(".");
		return builder.toString();
	}

    /**
     * Return positive if in the future, negative if in the past
     */
    public static float daysFromNow(Date date)
    {
        if (date == null)
            return 0;
        return (date.getTime() - (new Date()).getTime()) / 24.0f / 60.0f / 60.0f / 1000.0f;
    }

    /**
     * Return positive if in the future, negative if in the past
     */
    public static float hoursFromNow(Date date)
    {
        if (date == null)
            return 0;
        return (date.getTime() - (new Date()).getTime()) / 60.0f / 60.0f / 1000.0f;
    }

    /**
     * Return positive if in the future, negative if in the past
     */
    public static float minutesFromNow(Date date)
    {
        if (date == null)
            return 0;
        return (date.getTime() - (new Date()).getTime()) / 60.0f / 1000.0f;
    }

    /**
     * Return positive if in the future, negative if in the past
     */
    public static float secondsFromNow(Date date)
    {
        if (date == null)
            return 0;
        return (date.getTime() - (new Date()).getTime()) / 1000.0f;
    }

    /**
     * Return positive if in the date contains zero time for the day (midnight)
     */
    public static boolean isMidnight(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        return hours == 0 && minutes == 0 && seconds == 0;
    }


    /**
     * Return new datetime object with given offset
     */
    public static Date dateWithYearOffset(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, offset);
        return calendar.getTime();
    }

    public static Date dateWithMonthOffset(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, offset);
        return calendar.getTime();
    }

    public static Date dateWithDayOffset(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        return calendar.getTime();
    }

    public static Date dateWithHourOffset(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, offset);
        return calendar.getTime();
    }

    public static Date dateWithMinuteOffset(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, offset);
        return calendar.getTime();
    }

    public static Date dateWithSecondOffset(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, offset);
        return calendar.getTime();
    }

    /**
     * Check date time is not in 1970
     */
    public static boolean checkSanityDate(Date datetime) {
        if (datetime == null)
            return false;
        return datetime.getTime() > 0;
    }

    /**
     * Check date time is this week
     */
    public static boolean isThisWeek(Date datetime) {
        if (datetime == null || !isThisYear(datetime))
            return false;

        int curWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        Calendar calendar = new GregorianCalendar().getInstance();
        calendar.setTime(datetime);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        return curWeek - week == 0;
    }

    /**
     * Check date time is last week
     */
    public static boolean isLastWeek(Date datetime) {
        if (datetime == null || !isThisYear(datetime))
            return false;

        int curWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        Calendar calendar = new GregorianCalendar().getInstance();
        calendar.setTime(datetime);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        return curWeek - week == 1;
    }

    /**
     * Check date time is next week
     */
    public static boolean isNextWeek(Date datetime) {
        if (datetime == null || !isThisYear(datetime))
            return false;

        int curWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        Calendar calendar = new GregorianCalendar().getInstance();
        calendar.setTime(datetime);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        return week - curWeek == 1;
    }

    /**
     * Check date time is this month
     */
    public static boolean isThisMonth(Date datetime) {
        if (datetime == null || !isThisYear(datetime))
            return false;

        int curMonth = Calendar.getInstance().get(Calendar.MONTH);

        Calendar calendar = new GregorianCalendar().getInstance();
        calendar.setTime(datetime);
        int month = calendar.get(Calendar.MONTH);

        return curMonth - month == 0;
    }

    /**
     * Check date time is last month
     */
    public static boolean isLastMonth(Date datetime) {
        if (datetime == null || !isThisYear(datetime))
            return false;

        int curMonth = Calendar.getInstance().get(Calendar.MONTH);

        Calendar calendar = new GregorianCalendar().getInstance();
        calendar.setTime(datetime);
        int month = calendar.get(Calendar.MONTH);

        return curMonth - month == 1;
    }

    /**
     * Check date time is next month
     */
    public static boolean isNextMonth(Date datetime) {
        if (datetime == null || !isThisYear(datetime))
            return false;

        int curMonth = Calendar.getInstance().get(Calendar.MONTH);

        Calendar calendar = new GregorianCalendar().getInstance();
        calendar.setTime(datetime);
        int month = calendar.get(Calendar.MONTH);

        return month - curMonth == 1;
    }

    /**
     * Check date time is this year
     */
    public static boolean isThisYear(Date datetime) {
        int curYear = Calendar.getInstance().get(Calendar.YEAR);

        Calendar calendar = new GregorianCalendar().getInstance();
        calendar.setTime(datetime);
        int year = calendar.get(Calendar.YEAR);

        return curYear - year == 0;
    }

    /**
     * Check date time is in the future
     */
    public static boolean isFuture(Date datetime) {
        Date now = new Date();
        return datetime.getTime() > now.getTime();
    }

    /**
     * Check date time is in the past
     */
    public static boolean isPast(Date datetime) {
        Date now = new Date();
        return datetime.getTime() < now.getTime();
    }

    public static void CalendarEvetnAndroid(Activity activity, String prefill, Date datetime) {
        if(activity == null)
            return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(datetime);

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", prefill);
        activity.startActivity(intent);
    }

    public static Date getDate(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return calendar.getTime();
    }
}
