package com.joelcamargojr.androidhub.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Entity(tableName = "episodes")
@Parcel
public class Episode {

    @PrimaryKey
    @SerializedName("title")
    @NonNull
    public String title;
    @SerializedName("audio")
    public String audioUrl;
    @SerializedName("audio_length")
    public int audio_length;
    @SerializedName("id")
    public String id;
    @SerializedName("description")
    public String description;
    @SerializedName("pub_date_ms")
    public long date;
    @SerializedName("listennotes_url")
    public String listennotes_url;
    public long last_position;

    // empty constructor for Parceler
    @Ignore
    public Episode() {
    }

    @Ignore
    public Episode(@NonNull String title, String audioUrl, int audio_length, String id, String description,
                   long date, String listennotes_url) {
        this.title = title;
        this.audioUrl = audioUrl;
        this.audio_length = audio_length;
        this.id = id;
        this.description = description;
        this.date = date;
        this.listennotes_url = listennotes_url;
    }

    // Constructor for Room
    public Episode(@NonNull String title, String audioUrl, int audio_length, String id, String description,
                   long date, String listennotes_url, long last_position) {
        this.title = title;
        this.audioUrl = audioUrl;
        this.audio_length = audio_length;
        this.id = id;
        this.description = description;
        this.date = date;
        this.listennotes_url = listennotes_url;
        this.last_position = last_position;
    }
}
