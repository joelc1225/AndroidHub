package com.joelcamargojr.androidhub.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.joelcamargojr.androidhub.model.Episode;

import timber.log.Timber;

// Database that will hold the user's favorite episodes
@Database(entities = {Episode.class}, version = 1, exportSchema = false)
public abstract class EpisodeDatabase extends RoomDatabase{

    private final static String DATABASE_NAME = "episodes";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static volatile EpisodeDatabase sInstance;

    public static EpisodeDatabase getInstance(Context context) {

        Timber.d("CREATING DATABASE");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        EpisodeDatabase.class, EpisodeDatabase.DATABASE_NAME).build();

                Timber.d(" FINISHED CREATING DATABASE");
            }
        }
        return sInstance;
    }

    public abstract EpisodeDao episodeDao();
}
