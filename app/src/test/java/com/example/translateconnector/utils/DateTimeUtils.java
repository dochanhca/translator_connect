package com.example.translateconnector.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;

import com.imoktranslator.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    //Server date-time format
    public static final String YMD_HMS_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_LONG_MONTH_YEAR_FORMAT = "dd MMMM yyyy";
    public static final String DATE_MONTH_YEAR_FORMAT = "dd/MM/yyyy";
    public static final String DMY_HOUR_MINUTES_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String DMY_HMS_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static final String TIME_HOUR_MINUTES_24 = "HH:mm";
    public static final String TIME_HOUR_MINUTES_12 = "hh:mm aa";
    public static final SimpleDateFormat SDF_DD_MMM_YYYY_H_MM_A = new SimpleDateFormat("dd MMM yyyy h:mm a", Locale.US);
    public static final int EQUAL_DATE = 0;
    public static final int AFTER_DATE = 1;
    public static final int BEFORE_DATE = 2;

    public static final String DEFAULT_TIME_ZONE = "GMT+07:00";


    public static String getYMDAsTextFormat(String date, String format) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(format);

            Date d = inputFormat.parse(date);
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_LONG_MONTH_YEAR_FORMAT);

            return sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getDMYFormat(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(YMD_HMS_FORMAT);

            Date d = inputFormat.parse(date);
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_MONTH_YEAR_FORMAT);

            return sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getTimeFormat(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(YMD_HMS_FORMAT);

            Date d = inputFormat.parse(date);
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_HOUR_MINUTES_24);

            return sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertDateToString(Date date, String format) {

        if (date == null) {
            return "";
        }

        // Create an instance of SimpleDateFormat used for formatting
        String reportDate = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);

            // Using DateFormat format method we can create a string
            // representation of a date with the defined format.
            reportDate = df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reportDate;
    }

    public static String getCurrentDate(String format) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return convertDateToString(date, format);
    }

    public static Date convertDateFromString(String date, String format) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(format);

            Date d = inputFormat.parse(date);

            return d;
        } catch (Exception e) {
            return Calendar.getInstance().getTime();
        }
    }


    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }


    public static String getTimeFormat24(String time, String format) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(format);

            Date d = inputFormat.parse(time);
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_HOUR_MINUTES_24);

            return sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertDateFormat(String date, String inFormat, String outFormat) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(inFormat);

            Date d = inputFormat.parse(date);
            SimpleDateFormat sdf = new SimpleDateFormat(outFormat);

            return sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int compareDate(String strDate, String strDateCompare, String inFormat) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
        Date date = null;
        Date dateCompare = null;
        if (strDate == null || strDateCompare == null) {
            return -1;
        }

        try {
            date = sdf.parse(strDate);
            dateCompare = sdf.parse(strDateCompare);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null || dateCompare == null) {
            return -1;
        }

        if (date.after(dateCompare)) {
            System.out.println("Date1 is after Date2");
            return AFTER_DATE;
        }

        if (date.before(dateCompare)) {
            System.out.println("Date1 is before Date2");
            return BEFORE_DATE;

        }

        if (date.equals(dateCompare)) {
            System.out.println("Date1 is equal Date2");
            return EQUAL_DATE;
        }
        return -1;
    }

    public static String convertTimeZone(String inputDate, String format) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat(format);
        sourceFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        Date parsed; // => Date is in UTC now
        try {
            parsed = sourceFormat.parse(inputDate);
        } catch (ParseException e) {
            parsed = Calendar.getInstance().getTime();
        }

        TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat destFormat = new SimpleDateFormat(format);
        destFormat.setTimeZone(tz);

        return destFormat.format(parsed);
    }


    public static CharSequence convertTimestamp(Context context, String miliseconds) {
        if (System.currentTimeMillis() - Long.parseLong(miliseconds) < 1000 * 60) {
            return context.getString(R.string.MH30_003);
        }
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(miliseconds), System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS);
    }

    public static CharSequence convertSocialNotificationDate(Context context, String createdAt) {
        Date date = convertDateFromString(convertTimeZone(createdAt, YMD_HMS_FORMAT), YMD_HMS_FORMAT);

        return convertTimestamp(context, String.valueOf(date.getTime()));
    }
}
