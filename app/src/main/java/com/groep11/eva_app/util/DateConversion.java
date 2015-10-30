package com.groep11.eva_app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConversion {
    private static final String sFormat = "yyyy-MM-dd";

    public static String formatDate(Date date) {
        return new SimpleDateFormat(sFormat).format(date);
    }

    public static String formatDateFromMillis(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return new SimpleDateFormat(sFormat).format(date);
    }

    public static Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat(sFormat).parse(dateString);
        } catch (ParseException e) {
        }
        return null;
    }
}
