package com.lorem_ipsum.models;

import android.text.format.DateUtils;

import com.lorem_ipsum.utils.DateTimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Originally.US on 10/7/14.
 */
public class Notification implements Comparable<Notification> {

    public Number id;
    public Number app_id;
    public Number client_id;
    public Number language_id;

    public Number type;
    public String message;
    public String site_url;
    public Date created_datetime;
    public Date sent_datetime;

    public boolean isRead;

    public String getRelativeDateString() {
        if (sent_datetime != null)
            return DateUtils.getRelativeTimeSpanString(created_datetime.getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        if (sent_datetime != null)
            return DateUtils.getRelativeTimeSpanString(sent_datetime.getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        return null;
    }

    public String getDateTimeString() {

        Date theDate = this.sent_datetime;
        if (theDate == null)
            theDate = this.created_datetime;
        if (theDate == null)
            return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(this.sent_datetime);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        //Expected output: 7th July 2014, 2.56pm
        String dayNumberSuffix = DateTimeUtils.getDayNumberSuffix(day);
        SimpleDateFormat dateFormat = new SimpleDateFormat(" d'" + dayNumberSuffix + "' MMMM yyyy, hh:mma");
        return dateFormat.format(theDate.getTime());
    }

    public int compareTo(Notification anotherObject) {

        if (anotherObject == null)
            return 1;
        Date theTime = this.sent_datetime;
        if (theTime == null)
            theTime = created_datetime;
        Date anotherTime = anotherObject.sent_datetime;
        if (anotherTime == null)
            anotherTime = created_datetime;
        if (theTime == null)
            return -1;
        if (anotherTime == null)
            return 1;

        if (theTime.getTime() < anotherTime.getTime()) {
            return 1;
        } else if (theTime.getTime() > anotherTime.getTime()) {
            return -1;
        } else {
            return 0;
        }
    }
}
