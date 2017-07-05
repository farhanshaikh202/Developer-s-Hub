package com.farhansoftware.developershub.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.farhansoftware.developershub.fragments.WizardP1;
import com.farhansoftware.developershub.fragments.WizardP2;
import com.farhansoftware.developershub.fragments.WizardP3;

/**
 * Created by Farhan on 27-10-2016.
 */

public class WizardPagerAdapter extends FragmentPagerAdapter {

    public WizardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
            return new WizardP1();

            case 1:
                return new WizardP2();

            case 2:
                return new WizardP3();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "one";
            case 1:
                return "two";

            case 2:
                return "three";
            default:
                return "";
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
}