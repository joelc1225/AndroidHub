package com.joelcamargojr.androidhub;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    public MyViewPagerAdapter (FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // todo return fragments
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
