package com.joelcamargojr.androidhub.data;

import android.os.AsyncTask;

import com.joelcamargojr.androidhub.model.Episode;
import com.joelcamargojr.androidhub.model.Podcast;
import com.joelcamargojr.androidhub.room.EpisodeDao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class Repository {

    // For singleton instantiation
    private static EpisodeDao mEpisodeDao;
    private NetworkDatasource mNetworkDatasource;
    private static final Object LOCK = new Object();
    private static Repository sInstance;

    private Repository(EpisodeDao episodeDao, NetworkDatasource networkDatasource) {
        mEpisodeDao = episodeDao;
        mNetworkDatasource = networkDatasource;

    }

    public synchronized static Repository getInstance(EpisodeDao episodeDao, NetworkDatasource networkDatasource) {
        Timber.d("Getting the repository instance");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Repository(episodeDao, networkDatasource);
                Timber.d("Made new repository");
            }
        }
        return sInstance;
    }

    // Gets Podcast episodes for the MainActivity's RecyclerView
    public MutableLiveData<Podcast> getPodcastFromApi() {
        Timber.d("About to getPodcastfromAPI INSIDE REPO");
        return mNetworkDatasource.getPodcastDataFromApi();
    }

    // Gets the episodes from the database
    public LiveData<List<Episode>> getFaveEpisodesList() {
        return mEpisodeDao.getAllFavoriteEpisodes();
    }

    // Inserts new favorite episode to Room database
    public void insertFavorite(Episode episode) {
        Timber.d("STARTING INSERT METHOD REPO");
        new insertAsyncTask().execute(episode);
    }

    // Deletes episode from favorites database
    public void deleteFavorite(Episode episode) {
        Timber.d("STARTING INSERT METHOD REPO");
        new deleteAsyncTask().execute(episode);
    }

    // Gets single episode from database
    public Episode getSingleEpisodeById(String id) {
        Timber.d("QUERYING DB FOR SINGLE ITEM FROM REPO");
        return mEpisodeDao.getSingleEpisodeById(id);
    }

    // Fulfills rubric requirement of using asyncTask
    // Adds episode to favorites database
    private static class insertAsyncTask extends AsyncTask<Episode, Void, Void> {

        // Constructor
        insertAsyncTask() {}

        @Override
        protected Void doInBackground(final Episode... params) {
            Timber.d("INSIDE INSERT DIB: %s", params[0].title);
            mEpisodeDao.insertEpisode(params[0]);
            return null;
        }
    }

    // Fulfills rubric requirement of using asyncTask
    // Deletes episode from favorites database
    private static class deleteAsyncTask extends AsyncTask<Episode, Void, Void> {

        // Constructor
        deleteAsyncTask() {}

        @Override
        protected Void doInBackground(final Episode... params) {
            Timber.d("INSIDE DELETE DIB: %s", params[0].title);
            mEpisodeDao.deleteEpisode(params[0]);
            return null;
        }
    }
}
