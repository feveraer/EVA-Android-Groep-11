package com.groep11.eva_app.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateConversion {

    public static String formatDate(Date date) {
        return DateFormat.getDateInstance().format(date);
    }

    public static String formatDateFromMillis(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

    public static Date parseDate(String dateString) {
        try {
            return DateFormat.getDateInstance().parse(dateString);
        } catch (ParseException e) {
        }
        return null;
    }
}
