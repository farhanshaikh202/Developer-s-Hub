package com.farhansoftware.developershub.interfaces;

import android.view.View;

import com.farhansoftware.developershub.models.PostModel;

/**
 * Created by Farhan on 07-03-2017.
 */

public interface OnPostItemViewClickListener{
    void onClick(View v, PostModel model);
}
