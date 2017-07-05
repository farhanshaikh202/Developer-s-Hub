package com.farhansoftware.developershub.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Farhan on 08-03-2017.
 */

public class MyUtils {
    static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    static DateFormat formatter2 = new SimpleDateFormat("dd MMM yyyy");
    static DateFormat formatter3 = new SimpleDateFormat("hh:mm aaa");
    public static String dateFormat(Date date){
        return formatter2.format(date);
    }

    public static String timeFormat(Date date){
        return formatter3.format(date);
    }

    public static String dateFormat(String servertime){
        Date date = null;
        try {
            date = formatter.parse(servertime);
            return formatter2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String timeFormat(String servertime){
        Date date = null;
        try {
            date = formatter.parse(servertime);
            return formatter3.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
