package com.joelcamargojr.androidhub;

import android.app.Application;

import com.joelcamargojr.androidhub.di.AppComponent;
import com.joelcamargojr.androidhub.di.AppModule;
import com.joelcamargojr.androidhub.di.DaggerAppComponent;

import timber.log.Timber;

public class AndroidHubApplication extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
