package com.joelcamargojr.androidhub.viewModels;

import com.joelcamargojr.androidhub.data.Repository;
import com.joelcamargojr.androidhub.model.Episode;
import com.joelcamargojr.androidhub.model.Podcast;

import java.util.ArrayList;
import java.util.Objects;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private final Repository mRepository;
    private static MutableLiveData<Podcast> mPodcastData = new MutableLiveData<>();
    @SuppressWarnings("CanBeFinal")
    private static MutableLiveData<ArrayList<Episode>> mEpisodes = new MutableLiveData<>();

    MainActivityViewModel(Repository repository) {
        mRepository = repository;
        mPodcastData = mRepository.getPodcastFromApi();
    }


    public MutableLiveData<Podcast> getPodcastData() {
        return mPodcastData;
    }

    public void setEpisodesList() {
        mEpisodes.setValue(Objects.requireNonNull(mPodcastData.getValue()).episodeArrayList);
    }

    public MutableLiveData<ArrayList<Episode>> getEpisodesList() {
        return mEpisodes;
    }

    public MutableLiveData<Podcast> retryApiCall() {
        return mRepository.getPodcastFromApi();
    }
}
