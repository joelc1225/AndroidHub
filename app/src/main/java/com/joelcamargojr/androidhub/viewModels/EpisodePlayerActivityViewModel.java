package com.joelcamargojr.androidhub.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.joelcamargojr.androidhub.data.NetworkDatasource;
import com.joelcamargojr.androidhub.model.Podcast;

public class EpisodePlayerActivityViewModel extends AndroidViewModel {

    // Episode the user is looking at
    private Podcast mPodcast;
    private NetworkDatasource mNetworkDatasource;

    public EpisodePlayerActivityViewModel(Application application) {
        super(application);
        mPodcast = getPodcast();
        mNetworkDatasource = new NetworkDatasource(application);
    }

    public Podcast getPodcast() {
        if (mPodcast == null) {
            mPodcast = mNetworkDatasource.getPodcast();
        }
        return mPodcast;
    }
}
