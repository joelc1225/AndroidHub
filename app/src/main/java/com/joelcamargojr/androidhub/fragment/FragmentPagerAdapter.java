package com.joelcamargojr.androidhub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.data.FakeDataSource;
import com.joelcamargojr.androidhub.model.Article;
import com.joelcamargojr.androidhub.model.Podcast;

import org.parceler.Parcels;

import java.util.ArrayList;

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    // Will give us access to our string resources
    private Context mContext;

    // Gets fake data and creates bundle before sending to Frags
    private ArrayList<Article> articleList2 = FakeDataSource.getFakeArticleList2();

    // PODCAST TEST
    private Podcast fragPodcast;

    private Bundle bundle2 = new Bundle();
    private Bundle bundle3 = new Bundle();

    private WatchFragment watchFragment = new WatchFragment();
    private ListenFragment listenFragment = new ListenFragment();

    public FragmentPagerAdapter(FragmentManager fm, Context context, Podcast fragPodcast) {
        super(fm);
        this.mContext = context;
        this.fragPodcast = fragPodcast;
    }

    @Override
    public Fragment getItem(int position) {

        // puts fake test data into bundle
        bundle2.putParcelable("articleList2", Parcels.wrap(articleList2));
        bundle3.putParcelable("fragPodcast", Parcels.wrap(fragPodcast));

        switch (position) {
            case 0:
                watchFragment.setArguments(bundle2);
                return watchFragment;
            case 1:
                listenFragment.setArguments(bundle3);
                return listenFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.watch_label);
            case 1:
                return mContext.getResources().getString(R.string.listen_label);
        }
        return null;
    }

}
