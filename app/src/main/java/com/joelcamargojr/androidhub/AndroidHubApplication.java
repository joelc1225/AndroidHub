package com.joelcamargojr.androidhub;

import android.app.Application;

import timber.log.Timber;

class AndroidHubApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
