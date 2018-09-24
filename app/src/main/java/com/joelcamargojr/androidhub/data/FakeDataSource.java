package com.joelcamargojr.androidhub.data;

import com.joelcamargojr.androidhub.model.Article;

import java.util.ArrayList;

public class FakeDataSource {

    public static ArrayList<Article> articleList2 = new ArrayList<>();
    public static ArrayList<Article> articleList3 = new ArrayList<>();

    public static ArrayList<Article> getFakeArticleList2() {
        articleList2.add(new Article("Title 1", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList2.add(new Article("Title 2", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList2.add(new Article("Title 3", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList2.add(new Article("Title 4", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList2.add(new Article("Title 5", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList2.add(new Article("Title 6", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList2.add(new Article("Title 7", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList2.add(new Article("Title 8", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList2.add(new Article("Title 9", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList2.add(new Article("Title 10", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        return articleList2;
    }

    public static ArrayList<Article> getFakeArticleList3() {
        articleList3.add(new Article("Title 1", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList3.add(new Article("Title 2", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList3.add(new Article("Title 3", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList3.add(new Article("Title 4", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList3.add(new Article("Title 5", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList3.add(new Article("Title 6", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList3.add(new Article("Title 7", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList3.add(new Article("Title 8", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList3.add(new Article("Title 9", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        articleList3.add(new Article("Title 10", "https://media.pitchfork.com/photos/5aabd03c7e80eb754031ef32/2:1/w_790/Run-the-Jewels-Rick-and-Morty.jpg"));
        return articleList3;
    }
}
