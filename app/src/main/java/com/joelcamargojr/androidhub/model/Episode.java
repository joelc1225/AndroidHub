package com.joelcamargojr.androidhub.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Episode {

    @SerializedName("title")
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

    // empty constructor for Parceler
    public Episode() {
    }

    public Episode(String title, String audioUrl, int audio_length, String id, String description, long date) {
        this.title = title;
        this.audioUrl = audioUrl;
        this.audio_length = audio_length;
        this.id = id;
        this.description = description;
        this.date = date;
    }

}
