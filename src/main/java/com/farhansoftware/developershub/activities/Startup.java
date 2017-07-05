package com.farhansoftware.developershub.activities;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.farhansoftware.developershub.R;

public class Startup extends AppCompatActivity {

    private TextView SkipText;
    private ImageView TitleImg;
    private ImageView RegisterImg;
    private ImageView LoginImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        TitleImg =(ImageView)findViewById(R.id.startup_title_imgview);

        RegisterImg =(ImageView)findViewById(R.id.startup_register_imgview);

        RegisterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        LoginImg = (ImageView)findViewById(R.id.startup_login_imgview);

        LoginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        SkipText = (TextView)findViewById(R.id.startup_skip_textview);
        SkipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("loginskipped",false).apply();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


    }
}