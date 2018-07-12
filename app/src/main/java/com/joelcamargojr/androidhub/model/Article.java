package com.joelcamargojr.androidhub.model;

import org.parceler.Parcel;

@Parcel
public class Article {

    String mTitle;
    String mImageUrl;

    public Article (String title, String imageUrl) {
        this.mTitle = title;
        this.mImageUrl = imageUrl;
    }

    // Empty constructor needed by Parceler library
    public Article() {
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}
