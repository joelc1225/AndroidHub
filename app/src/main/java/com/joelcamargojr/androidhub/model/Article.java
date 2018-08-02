package com.joelcamargojr.androidhub.model;

import org.parceler.Parcel;

@Parcel
public class Article {

    public String title;
    public String imageUrl;

    public Article (String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    // Empty constructor needed by Parceler library
    public Article() { }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
