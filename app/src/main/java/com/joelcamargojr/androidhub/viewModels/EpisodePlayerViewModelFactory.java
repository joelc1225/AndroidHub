package com.joelcamargojr.androidhub.viewModels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.joelcamargojr.androidhub.data.Repository;
import com.joelcamargojr.androidhub.model.Episode;

public class EpisodePlayerViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Repository mRepository;
    private Episode mEpisode;

    public EpisodePlayerViewModelFactory(Repository repository, Episode episode) {
        this.mRepository = repository;
        this.mEpisode = episode;
    }

    @SuppressWarnings("unchecked cast warning")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EpisodePlayerActivityViewModel(mRepository, mEpisode);
    }
}
