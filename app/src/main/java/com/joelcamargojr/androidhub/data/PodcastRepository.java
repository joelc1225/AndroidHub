package com.joelcamargojr.androidhub.data;

import com.joelcamargojr.androidhub.model.Podcast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class PodcastRepository implements PodcastDataSource {

    private Podcast podcast;
    private PodcastAPIEndpoints podcastAPIInterface =
            RetrofitApi.getPodcastClient().create(PodcastAPIEndpoints.class);

    // empty constructor
    public PodcastRepository() { }

    @Override
    public Podcast getFragmentedPodcast(String podcastId) {

        // Calls API to get the list of recent Podcast episodes for given fragPodcast ID
        Call<Podcast> call =
                podcastAPIInterface.getFragmentedPodcastList(podcastId);

        call.enqueue(new Callback<Podcast>() {
            @Override
            public void onResponse(Call<Podcast> call, Response<Podcast> response) {
                int statusCode = response.code();
                Timber.d("STATUS CODE IS: " + statusCode);
                Timber.d(response.body().toString());
                podcast = response.body();
            }

            @Override
            public void onFailure(Call<Podcast> call, Throwable t) {
                Timber.d("ERROR: " + t.getLocalizedMessage());
            }
        });

        return podcast;
    }
}
