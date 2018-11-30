package com.joelcamargojr.androidhub.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.ParcelProperty;

import java.util.ArrayList;

@Parcel(Parcel.Serialization.FIELD)
public class Podcast {

    @SerializedName("description")
    public String description;
    @SerializedName("title")
    public String title;
    @SerializedName("image")
    public String image;
    @SerializedName("website")
    public String website;
    @SerializedName("episodes")
    public ArrayList<Episode> episodeArrayList;
    @SerializedName("thumbnail")
    public String thumbnail;

    @ParcelConstructor
    public Podcast(String description, String title, String image, String website, @ParcelProperty("episodes") ArrayList<Episode> episodeList, String thumbnail) {
        this.description = description;
        this.title = title;
        this.image = image;
        this.website = website;
        this.episodeArrayList = episodeList;
        this.thumbnail = thumbnail;
    }

    // Needed for Parceler to handle inner customObject
    @ParcelProperty("episodes")
    public ArrayList<Episode> getEpisodeArrayList() {
        return episodeArrayList;
    }

}