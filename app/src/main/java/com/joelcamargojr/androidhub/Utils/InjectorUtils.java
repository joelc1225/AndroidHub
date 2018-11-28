package com.joelcamargojr.androidhub.Utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.joelcamargojr.androidhub.data.NetworkDatasource;
import com.joelcamargojr.androidhub.data.Repository;
import com.joelcamargojr.androidhub.room.EpisodeDatabase;

public class InjectorUtils {

    /**
     * Provides static methods to inject various classes needed for app
     * one instantiation per app run through
     */

    @SuppressLint("StaticFieldLeak")

    public static Repository provideRepository(Context context) {

        EpisodeDatabase episodeDatabase = EpisodeDatabase.getInstance(context.getApplicationContext());
        NetworkDatasource networkDatasource =
                provideNetworkDatasource(context.getApplicationContext());

        return Repository.getInstance(episodeDatabase.episodeDao(), networkDatasource);
    }

    private static NetworkDatasource provideNetworkDatasource(Context context) {
        return NetworkDatasource.getInstance(context);
    }
}
