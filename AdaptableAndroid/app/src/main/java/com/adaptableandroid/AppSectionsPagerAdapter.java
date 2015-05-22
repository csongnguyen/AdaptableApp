package com.adaptableandroid;

/**
 * Created by Connie on 5/12/2015.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
 * sections of the app.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();


    public AppSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {

//        switch (i) {
//            case 0:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
            if(!fragmentMap.containsKey(i)){
                Fragment fragment = new LaunchpadSectionFragment();
                Bundle args = new Bundle();
                args.putString(LaunchpadSectionFragment.ARG_DISASTER, StringUtils.disasterTypes[i]);
                args.putInt(LaunchpadSectionFragment.ARG_DISASTER_NUMBER, i+1);
                args.putInt(LaunchpadSectionFragment.ARG_TOTAL_DISASTERS, getCount());
                fragment.setArguments(args);

                fragmentMap.put(i, fragment);

                return fragment;
            }
            else{
                return fragmentMap.get(i);
            }

//            default:
////                // The other sections of the app are dummy placeholders.
////                Fragment fragment = new DummySectionFragment();
////                Bundle args = new Bundle();
////                args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
////                fragment.setArguments(args);
//                return fragment;
//        }
    }

    @Override
    public int getCount() {
        return StringUtils.disasterTypes.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Section " + (position + 1);
    }
}

