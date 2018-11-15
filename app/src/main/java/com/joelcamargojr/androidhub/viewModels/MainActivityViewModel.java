package com.joelcamargojr.androidhub.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.joelcamargojr.androidhub.model.Episode;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    // List of Episodes the user is looking at
    private MutableLiveData<List<Episode>> mEpisodes;

    public MainActivityViewModel() {
        mEpisodes = new MutableLiveData<>();
    }

    public MutableLiveData<List<Episode>> getEpisodes() {
        return mEpisodes;
    }

    public void setEpisodeList(List<Episode> episodeList) {
        mEpisodes.postValue(episodeList);
    }
}
