package com.joelcamargojr.androidhub.viewModels;

import android.arch.lifecycle.ViewModel;

import com.joelcamargojr.androidhub.model.Episode;

public class EpisodePlayerActivityViewModel extends ViewModel {

    // Weather forecast the user is looking at
    private Episode mEpisode;

    public EpisodePlayerActivityViewModel() {

    }

    public Episode getEpisode() {
        return mEpisode;
    }

    public void setEpisode(Episode episode) {
        mEpisode = episode;
    }
}
