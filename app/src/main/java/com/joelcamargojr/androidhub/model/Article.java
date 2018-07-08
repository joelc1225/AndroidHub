package com.joelcamargojr.androidhub.model;

public class Article {

    private String mTitle;
    private String mImageUrl;

    public Article (String title, String imageUrl) {
        this.mTitle = title;
        this.mImageUrl = imageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}
