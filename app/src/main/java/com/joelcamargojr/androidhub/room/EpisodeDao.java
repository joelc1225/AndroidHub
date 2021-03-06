package com.joelcamargojr.androidhub.room;

import com.joelcamargojr.androidhub.model.Episode;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface EpisodeDao {

    // Queries for the whole list of favorite episodes
    @Query("SELECT * FROM episodes")
    LiveData<List<Episode>> getAllFavoriteEpisodes();

    // Queries for one episode item based off title ID
    // Used only to verify if an entry already exists in database
    @Query("SELECT * FROM episodes WHERE id =:id")
    Episode getSingleEpisodeById(String id);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertEpisode(Episode episode);

    @Delete
    void deleteEpisode(Episode episode);
}
