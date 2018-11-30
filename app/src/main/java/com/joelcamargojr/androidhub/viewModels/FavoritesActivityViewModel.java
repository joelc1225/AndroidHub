package com.joelcamargojr.androidhub.viewModels;

import com.joelcamargojr.androidhub.data.Repository;
import com.joelcamargojr.androidhub.model.Episode;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class FavoritesActivityViewModel extends ViewModel {

    private static LiveData<List<Episode>> mEpisodeList;

    FavoritesActivityViewModel(Repository repository) {
        mEpisodeList = repository.getFaveEpisodesList();
    }


    public LiveData<List<Episode>> getFavoriteEpisodesList() {
        return mEpisodeList;
    }

}
