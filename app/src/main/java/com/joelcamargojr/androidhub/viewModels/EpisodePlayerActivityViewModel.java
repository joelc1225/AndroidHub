package com.joelcamargojr.androidhub.viewModels;

import com.joelcamargojr.androidhub.data.Repository;
import com.joelcamargojr.androidhub.model.Episode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

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
        Timber.d("STARTING INSERT METHOD VM");
        mRepository.insertFavorite(episode);
    }

    public void deleteFavorite(Episode episode) {
        Timber.d("STARTING DELETE METHOD VM");
        mRepository.deleteFavorite(episode);
    }

    public LiveData<Episode> checkIfFavorite(String episodeTitle) {
        return mRepository.checkIfFavorite(episodeTitle);
    }

    public void setEpisodeMutableLiveData(Episode episode) {
        mEpisodeMutableLiveData.setValue(episode);
    }

}
