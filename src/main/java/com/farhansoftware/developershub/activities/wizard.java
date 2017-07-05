package com.farhansoftware.developershub.activities;

import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.adapters.WizardPagerAdapter;

public class wizard extends AppCompatActivity {

    private ViewPager pager;
    private WizardPagerAdapter pagerAdapter;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        pager =(ViewPager)findViewById(R.id.viewpager);
        btn=(Button)findViewById(R.id.wizardBtn);


        pagerAdapter=new WizardPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==pagerAdapter.getCount()-1){
                    btn.setBackgroundColor(Color.CYAN);
                    btn.setText("Lest's go");
                }else {
                    btn.setBackgroundColor(Color.WHITE);
                    btn.setText("next");
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pager.getCurrentItem()==pagerAdapter.getCount()-1){
                    finish();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("firstlaunch",false).apply();
                    startActivity(new Intent(getApplicationContext(),Startup.class));
                }else {
                    pager.setCurrentItem(pager.getCurrentItem()+1);
                }
            }
        });

    }



}
