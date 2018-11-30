package com.joelcamargojr.androidhub.data;

import android.content.Context;
import android.widget.Toast;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.model.Podcast;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NetworkDatasource {

    private static Podcast fragPodcast;
    private static MutableLiveData<Podcast> mPodcastData = new MutableLiveData<>();
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
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDatasource(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    public MutableLiveData<Podcast> getPodcastDataFromApi() {

        // sets podcast ids needed for api calls
        String fragmentedPodcastId = mContext.getString(R.string.fragmentedPodcastId);

        PodcastAPIEndpoints podcastAPIInterface = RetrofitApi.getPodcastClient().create(PodcastAPIEndpoints.class);

        // gets list of episodes for given fragPodcast ID
        // Calls API to get the list of recent Podcast episodes for given fragPodcast ID
        call = podcastAPIInterface.getFragmentedPodcast(fragmentedPodcastId);

        call.enqueue(new Callback<Podcast>() {
            @Override
            public void onResponse(Call<Podcast> call, Response<Podcast> response) {
                if (response.isSuccessful()) {
                    int statusCode = response.code();
                    fragPodcast = response.body();
                    mPodcastData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(final Call<Podcast> call, Throwable t) {
                Timber.d("ONFAILURE ERROR: %s", t.getLocalizedMessage());
                Toast.makeText(mContext, "No Response! Try again later ", Toast.LENGTH_SHORT).show();
                mPodcastData.setValue(null);
            }
        });

        return mPodcastData;
    }
}