package com.joelcamargojr.androidhub.viewModels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.joelcamargojr.androidhub.data.Repository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Repository mRepository;

    public MainViewModelFactory(Repository repository) {
        this.mRepository = repository;
    }

    @SuppressWarnings("unchecked cast warning")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(mRepository);
    }
}
