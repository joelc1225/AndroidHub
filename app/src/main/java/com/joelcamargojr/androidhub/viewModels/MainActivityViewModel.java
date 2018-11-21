package com.joelcamargojr.androidhub.viewModels;

import android.arch.lifecycle.ViewModel;

import com.joelcamargojr.androidhub.data.Repository;
import com.joelcamargojr.androidhub.model.Episode;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final Repository mRepository;
    private final List<Episode> mEpisodes;

    public MainActivityViewModel(Repository repository) {
        mRepository = repository;
        mEpisodes = mRepository.getAllEpisodesFromApi();
    }


    public List<Episode> getEpisodes() {
        return mEpisodes;
    }
}
