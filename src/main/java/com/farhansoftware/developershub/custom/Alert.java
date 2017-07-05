package com.farhansoftware.developershub.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Farhan on 02-02-2017.
 *
 */

public class Alert {
    static AlertDialog.Builder builder;
    public static void show(Context context,String msg){
        builder=new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
