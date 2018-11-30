package com.joelcamargojr.androidhub.viewModels;

import com.joelcamargojr.androidhub.data.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FavoritesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Repository mRepository;

    public FavoritesViewModelFactory(Repository repository) {
        this.mRepository = repository;
    }

    @SuppressWarnings("unchecked cast warning")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavoritesActivityViewModel(mRepository);
    }
}
