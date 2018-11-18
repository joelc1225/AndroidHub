package com.joelcamargojr.androidhub.data;

import android.app.Application;
import android.os.AsyncTask;

import com.joelcamargojr.androidhub.model.Episode;
import com.joelcamargojr.androidhub.room.EpisodeDao;
import com.joelcamargojr.androidhub.room.EpisodeDatabase;

import java.util.List;

public class Repository {

    private List<Episode> mEpisodesList;
    private List<Episode> mFaveEpisodesList;
    private EpisodeDao mEpisodeDao;

    public Repository(Application application) {
        EpisodeDatabase db = EpisodeDatabase.getInstance(application);
        mEpisodeDao = db.episodeDao();
        this.mFaveEpisodesList = mEpisodeDao.getAllEpisodes();
        this.mEpisodesList = getAllEpisodes();
    }

    // Gets Podcast episodes for the MainActivity's RecyclerView
    List<Episode> getAllEpisodes() {
        return mEpisodesList;
    }

    // Gets the episodes from the FavoriteEpisod
    List<Episode> getFaveEpisodesList() {
        return mFaveEpisodesList;
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
