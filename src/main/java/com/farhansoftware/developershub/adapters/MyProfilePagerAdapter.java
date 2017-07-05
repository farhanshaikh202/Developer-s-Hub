package com.farhansoftware.developershub.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.farhansoftware.developershub.fragments.MainCategoryFragment;
import com.farhansoftware.developershub.fragments.MainPostsFragment;
import com.farhansoftware.developershub.fragments.ProfileLikedPostsFragment;
import com.farhansoftware.developershub.fragments.ProfileSelfCommentsFragment;
import com.farhansoftware.developershub.fragments.ProfileSelfPostsFragment;

/**
 * Created by Farhan on 23-03-2017.
 */

public class MyProfilePagerAdapter extends FragmentPagerAdapter {
    public MyProfilePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    ProfileLikedPostsFragment liked;
    ProfileSelfPostsFragment posts;

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                posts=posts==null?new ProfileSelfPostsFragment():posts;
                return posts;

            case 1:

                liked=liked==null?new ProfileLikedPostsFragment():liked;
                return liked;

            case 2:
                return new ProfileSelfCommentsFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
