package com.joelcamargojr.androidhub.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.joelcamargojr.androidhub.ListenFragment;
import com.joelcamargojr.androidhub.ReadFragment;
import com.joelcamargojr.androidhub.WatchFragment;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ReadFragment();
            case 1:
                return new WatchFragment();
            case 2:
                return new ListenFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
