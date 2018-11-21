package com.joelcamargojr.androidhub.data;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.joelcamargojr.androidhub.model.Episode;
import com.joelcamargojr.androidhub.model.Podcast;
import com.joelcamargojr.androidhub.room.EpisodeDao;

import java.util.List;

import timber.log.Timber;

public class Repository {

    // For singleton instantiation
    private EpisodeDao mEpisodeDao;
    private NetworkDatasource mNetworkDatasource;
    private static final Object LOCK = new Object();
    private static Repository sInstance;
    private boolean mInitialized = false;

    private Podcast mPodcast;

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
    public List<Episode> getAllEpisodesFromApi() {

        Timber.d("About to getAllEpisodesfromAPI INSIDE REPO");
        if (mPodcast == null) {
            mPodcast = mNetworkDatasource.getPodcast();
        }
        return mPodcast.episodeArrayList;
    }

    // Gets the episodes from the database
    public LiveData<List<Episode>> getFaveEpisodesList() {
       return mEpisodeDao.getAllFavoriteEpisodes();
    }

    // Inserts new favorite episode to Room database
    public void insertFavorite(Episode episode) {
        new insertAsyncTask(mEpisodeDao).execute(episode);
    }

    // Deletes episode from favorites database
    public void deleteFavorite(Episode episode) {
        new deleteAsyncTask(mEpisodeDao).execute(episode);
    }

    // Fulfills rubric requirement of using asyncTask
    // Adds episode to favorites database
    private static class insertAsyncTask extends AsyncTask<Episode, Void, Void> {

        private EpisodeDao mAsyncTaskDao;

        insertAsyncTask(EpisodeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Episode... params) {
            mAsyncTaskDao.insertEpisode(params[0]);
            return null;
        }
    }

    // Fulfills rubric requirement of using asyncTask
    // Deletes episode from favorites database
    private static class deleteAsyncTask extends AsyncTask<Episode, Void, Void> {

        private EpisodeDao mAsyncTaskDao;

        deleteAsyncTask(EpisodeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Episode... params) {
            mAsyncTaskDao.deleteEpisode(params[0]);
            return null;
        }
    }
}
