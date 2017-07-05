package com.farhansoftware.developershub.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;

/**
 * Created by Farhan on 02-02-2017.
 */

public class Loading {

    private ProgressDialog pd;

    public void show(Context context, String msd){
        pd=new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage(msd);
        pd.setIndeterminate(true);
        pd.show();
    }

    public void updatemsg(String msg){
        pd.setMessage(msg);
    }

    public void hide(){
        pd.dismiss();
    }
}
