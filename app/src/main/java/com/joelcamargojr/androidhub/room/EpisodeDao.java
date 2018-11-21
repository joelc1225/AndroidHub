package com.joelcamargojr.androidhub.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.joelcamargojr.androidhub.model.Episode;

import java.util.List;

@Dao
public interface EpisodeDao {

    // Queries for the whole list of favorite episodes
    @Query("SELECT * FROM episodes")
    LiveData<List<Episode>> getAllFavoriteEpisodes();

    // Queries for one episode item based off title ID
    // Used only to verify if an entry already exists in database
    @Query("SELECT * FROM episodes WHERE id = :id")
    Episode getFavoriteEpisodeById(String id);

    @Insert
    void insertEpisode(Episode episode);

    @Delete
    void deleteEpisode(Episode episode);
}
