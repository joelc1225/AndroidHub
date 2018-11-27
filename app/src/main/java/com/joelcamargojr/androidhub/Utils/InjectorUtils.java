package com.joelcamargojr.androidhub.Utils;

import android.content.Context;

import com.joelcamargojr.androidhub.data.NetworkDatasource;
import com.joelcamargojr.androidhub.data.Repository;
import com.joelcamargojr.androidhub.room.EpisodeDatabase;

public class InjectorUtils {

    /**
     *
     * Provides static methods to inject various classes needed for app
     * one instantiation per app run through
     *
     */

    public static Repository mRepository;
    NetworkDatasource mNetworkDatasource;

    public static Repository provideRepository(Context context) {

        if (mRepository == null) {
            EpisodeDatabase episodeDatabase = EpisodeDatabase.getInstance(context.getApplicationContext());
            NetworkDatasource networkDatasource =
                    provideNetworkDatasource(context.getApplicationContext());
            return Repository.getInstance(episodeDatabase.episodeDao(), networkDatasource);
        }
        return mRepository;

    }

    public static NetworkDatasource provideNetworkDatasource(Context context) {
        return NetworkDatasource.getInstance(context);
    }
}
