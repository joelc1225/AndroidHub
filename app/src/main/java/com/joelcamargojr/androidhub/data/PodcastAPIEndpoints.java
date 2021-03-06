package com.joelcamargojr.androidhub.data;

import com.joelcamargojr.androidhub.BuildConfig;
import com.joelcamargojr.androidhub.model.Podcast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface PodcastAPIEndpoints {


    @Headers({
            "X-ListenAPI-Key:" + BuildConfig.ApiKey,
            "Accept: application/json"
    })
    @GET("api/v2/podcasts/{podcast_id}/")
    Call<Podcast> getFragmentedPodcast(@Path("podcast_id") String id);
}
