package com.joelcamargojr.androidhub.di;

import android.app.Application;

import com.joelcamargojr.androidhub.ListenFragment;
import com.joelcamargojr.androidhub.MainActivity;
import com.joelcamargojr.androidhub.ReadFragment;
import com.joelcamargojr.androidhub.WatchFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component (modules = AppModule.class)
public interface AppComponent {

    void inject(ListenFragment listenFragment);
    void inject(ReadFragment readFragment);
    void inject(WatchFragment watchFragment);
    void inject(MainActivity mainActivity);

    Application application();
}
