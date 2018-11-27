package com.joelcamargojr.androidhub.viewModels;

import com.joelcamargojr.androidhub.data.Repository;
import com.joelcamargojr.androidhub.model.Episode;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EpisodePlayerActivityViewModel extends ViewModel {

    // Episode the user is looking at
    private Repository mRepository;
    private MutableLiveData<Episode> mEpisodeMutableLiveData;

    public EpisodePlayerActivityViewModel(Repository repository, Episode episode) {
        this.mRepository = repository;
        mEpisodeMutableLiveData = new MutableLiveData<>();
        mEpisodeMutableLiveData.setValue(episode);
    }

    public void insertFavorite(Episode episode) {
        mRepository.insertFavorite(episode);
    }

    public void deleteFavorite(Episode episode) {
        mRepository.deleteFavorite(episode);
    }
}
