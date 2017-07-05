package com.farhansoftware.developershub.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.farhansoftware.developershub.fragments.MainCategoryFragment;
import com.farhansoftware.developershub.fragments.MainPostsFragment;
import com.farhansoftware.developershub.fragments.WizardP1;
import com.farhansoftware.developershub.fragments.WizardP2;
import com.farhansoftware.developershub.fragments.WizardP3;

/**
 * Created by Farhan on 27-10-2016.
 *
 */

public class MainScreenPagerAdapter extends FragmentPagerAdapter {

    MainPostsFragment mp;
    public MainScreenPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MainPostsFragment getMainPostsFragment(){
        return mp;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
            return mp=new MainPostsFragment();

            case 1:
                return new MainCategoryFragment();

        }
        return null;
    }





    @Override
    public int getCount() {
        return 2;
    }
}