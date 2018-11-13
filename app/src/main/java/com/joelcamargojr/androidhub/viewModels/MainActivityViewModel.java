package com.joelcamargojr.androidhub.viewModels;

import android.arch.lifecycle.ViewModel;

import com.joelcamargojr.androidhub.model.Episode;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    // Weather forecast the user is looking at
    private List<Episode> mEpisodes;

    public MainActivityViewModel() {
    }

    public List<Episode> getEpisodes() {
        return mEpisodes;
    }

    public void setEpisodeList(List<Episode> episodeList) {
        mEpisodes = episodeList;
    }
}
