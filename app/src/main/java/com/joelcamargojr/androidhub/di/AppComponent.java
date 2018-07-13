package com.joelcamargojr.androidhub.di;

import com.joelcamargojr.androidhub.fragment.ListenFragment;
import com.joelcamargojr.androidhub.MainActivity;
import com.joelcamargojr.androidhub.fragment.ReadFragment;
import com.joelcamargojr.androidhub.fragment.WatchFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component (modules = AppModule.class)
public interface AppComponent {

    void inject(ListenFragment listenFragment);
    void inject(ReadFragment readFragment);
    void inject(WatchFragment watchFragment);
    void inject(MainActivity mainActivity);
}
