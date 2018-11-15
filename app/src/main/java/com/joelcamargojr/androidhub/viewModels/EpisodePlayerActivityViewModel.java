package com.joelcamargojr.androidhub.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.joelcamargojr.androidhub.model.Episode;

public class EpisodePlayerActivityViewModel extends ViewModel {

    // Episode the user is looking at
    private MutableLiveData<Episode> mEpisode;

    public EpisodePlayerActivityViewModel() {
        mEpisode = new MutableLiveData<>();
    }

    public MutableLiveData<Episode> getEpisode() {
        return mEpisode;
    }

    public void setEpisode(Episode episode) {
        mEpisode.postValue(episode);
    }
}
