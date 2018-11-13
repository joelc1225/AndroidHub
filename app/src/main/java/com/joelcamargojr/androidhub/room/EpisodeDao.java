package com.joelcamargojr.androidhub.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.joelcamargojr.androidhub.model.Episode;

import java.util.List;

@Dao
public interface EpisodeDao {

    @Query("SELECT * FROM episodes")
    List<Episode> getAllEpisodes();

    @Insert
    void insertEpisode(Episode episode);

    @Delete
    void deleteEpisode(Episode episode);
}
