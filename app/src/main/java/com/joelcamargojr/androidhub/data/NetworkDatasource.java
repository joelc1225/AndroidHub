package com.joelcamargojr.androidhub.data;

import android.content.Context;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.model.Podcast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NetworkDatasource {

    private Podcast fragPodcast;
    private Call<Podcast> call;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NetworkDatasource sInstance;
    private Context mContext;

    public NetworkDatasource(Context context) {
        this.mContext = context;
    }

    // Get singleton for this class
    public static NetworkDatasource getInstance(Context context) {
        Timber.d("Getting Network Datasource");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDatasource(context.getApplicationContext());
                Timber.d("Made new Network Datasource");
            }
        }
        return sInstance;
    }

    public Podcast getPodcast() {

        if (fragPodcast == null) {
            Timber.d("Podcast data is null. Making network request.");
            // sets podcast ids needed for api calls
            String fragmentedPodcastId = mContext.getString(R.string.fragmentedPodcastId);

            PodcastAPIEndpoints podcastAPIInterface = RetrofitApi.getPodcastClient().

                    create(PodcastAPIEndpoints.class);

            // gets list of episodes for given fragPodcast ID
            // Calls API to get the list of recent Podcast episodes for given fragPodcast ID
            call = podcastAPIInterface.getFragmentedPodcastList(fragmentedPodcastId);

            call.enqueue(new Callback<Podcast>() {
                @Override
                public void onResponse(Call<Podcast> call, Response<Podcast> response) {
                    int statusCode = response.code();
                    Timber.d("STATUS CODE IS: %s", statusCode);
                    fragPodcast = response.body();
                }

                @Override
                public void onFailure(final Call<Podcast> call, Throwable t) {
                    Timber.d("ERROR: %s", t.getLocalizedMessage());
                    retry();
                }

                private void retry() {
                    call.clone().enqueue(this);
                }

            });
        } else {
            Timber.d("Podcast already instantiated. Returning same data");
            return fragPodcast;
        }
        return fragPodcast;
    }
}

