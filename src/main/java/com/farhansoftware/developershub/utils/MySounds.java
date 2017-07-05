package com.farhansoftware.developershub.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.farhansoftware.developershub.R;

/**
 * Created by Farhan on 08-03-2017.
 */

public class MySounds {
    public static void click(Context context){
        try {
            MediaPlayer mp = MediaPlayer.create(context, R.raw.clicksound);
            mp.start();
        } catch(Exception e) { e.printStackTrace(); }
    }
}
