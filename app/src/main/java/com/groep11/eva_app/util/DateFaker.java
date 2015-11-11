package com.groep11.eva_app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;

public class DateFaker {
    private static final String DATE_KEY = "CURRENT_FAKE_DATE";

    private SharedPreferences spref;

    public DateFaker(Context context) {
        this.spref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Date getCurrentDate() {
        return new Date(spref.getLong(DATE_KEY, new Date().getTime()));
    }

    public void setCurrentDate(Date date){
        SharedPreferences.Editor editor = spref.edit();
        editor.putLong(DATE_KEY, date.getTime());
        editor.commit();
    }

    public void nextDay(){
        Calendar c = Calendar.getInstance();
        c.setTime(getCurrentDate());
        c.add(Calendar.DATE, 1);
        setCurrentDate(c.getTime());
    }

    public void reset(){
        SharedPreferences.Editor editor = spref.edit();
        editor.remove(DATE_KEY);
        editor.commit();
    }
}
