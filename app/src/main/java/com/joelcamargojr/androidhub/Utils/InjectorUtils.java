package com.joelcamargojr.androidhub.Utils;

import android.content.Context;

import com.joelcamargojr.androidhub.data.NetworkDatasource;
import com.joelcamargojr.androidhub.data.Repository;
import com.joelcamargojr.androidhub.room.EpisodeDatabase;

public class InjectorUtils {

    /**
     *
     * Provides static methods to inject various classes needed for app
     *
     */

    public static Repository provideRepository(Context context) {
        EpisodeDatabase episodeDatabase = EpisodeDatabase.getInstance(context.getApplicationContext());
        NetworkDatasource networkDatasource =
                NetworkDatasource.getInstance(context.getApplicationContext());
        return Repository.getInstance(episodeDatabase.episodeDao(), networkDatasource);
    }

    public static NetworkDatasource provideNetworkDatasource(Context context) {
        return NetworkDatasource.getInstance(context);
    }
}
