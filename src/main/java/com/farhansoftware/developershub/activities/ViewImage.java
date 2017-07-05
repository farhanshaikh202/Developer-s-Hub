package com.farhansoftware.developershub.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.farhansoftware.developershub.R;
import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView im=new ImageView(getApplicationContext());

        setContentView(im);
        Picasso.with(getApplicationContext()).load(getIntent().getStringExtra("url")).into(im);
    }
}
