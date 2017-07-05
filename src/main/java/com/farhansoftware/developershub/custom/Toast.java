package com.farhansoftware.developershub.custom;

import android.content.Context;

/**
 * Created by Farhan on 02-02-2017.
 */

public class Toast {
    public static void show(Context context,String msg){
        android.widget.Toast.makeText(context,msg, android.widget.Toast.LENGTH_SHORT).show();
    }
}
