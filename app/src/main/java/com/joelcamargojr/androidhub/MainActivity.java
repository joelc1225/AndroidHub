package com.joelcamargojr.androidhub;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.joelcamargojr.androidhub.data.PodcastAPIEndpoints;
import com.joelcamargojr.androidhub.data.RetrofitApi;
import com.joelcamargojr.androidhub.databinding.ActivityMainBinding;
import com.joelcamargojr.androidhub.model.Podcast;
import com.joelcamargojr.androidhub.recyclerview.MainRecyclerviewAdapter;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public Podcast fragPodcast;
    String fragmentedPodcastId;
    public PodcastAPIEndpoints podcastAPIInterface;
    private Call<Podcast> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create data binding instance for views
        final ActivityMainBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);

        // set up custom toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        binding.mainProgressbar.setVisibility(View.VISIBLE);


        String widgetAction = getIntent().getAction();
        if (Objects.equals(widgetAction, getString(R.string.widgetToastAction))) {
            Toast.makeText(this, R.string.widgetToastMessage, Toast.LENGTH_SHORT).show();
        }

        // sets podcast ids needed for api calls
        fragmentedPodcastId = getString(R.string.fragmentedPodcastId);

        podcastAPIInterface =
                RetrofitApi.getPodcastClient().create(PodcastAPIEndpoints.class);

        // gets list of episodes for given fragPodcast ID
        // Calls API to get the list of recent Podcast episodes for given fragPodcast ID
        call = podcastAPIInterface.getFragmentedPodcastList(fragmentedPodcastId);

        call.enqueue(new Callback<Podcast>() {
            @Override
            public void onResponse(Call<Podcast> call, Response<Podcast> response) {
                binding.mainProgressbar.setVisibility(View.INVISIBLE);
                int statusCode = response.code();
                Timber.d("STATUS CODE IS: %s", statusCode);
                fragPodcast = response.body();
                RecyclerView recyclerView = binding.recyListenFrag;
                MainRecyclerviewAdapter adapter = new MainRecyclerviewAdapter(fragPodcast, getApplicationContext());
                recyclerView.setAdapter(adapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);

                if (binding.retryButton.getVisibility() == View.VISIBLE) {
                    binding.retryButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(final Call<Podcast> call, Throwable t) {
                Timber.d("ERROR: %s", t.getLocalizedMessage());

                binding.mainProgressbar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "No connection. Try again later.", Toast.LENGTH_SHORT).show();

                binding.retryButton.setVisibility(View.VISIBLE);
                binding.retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.mainProgressbar.setVisibility(View.VISIBLE);
                        binding.retryButton.setVisibility(View.INVISIBLE);
                        retry();
                    }
                });
            }

            private void retry() {
                call.clone().enqueue(this);
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.action_find_a_coding_spot:
                Toast.makeText(this, "CODING SPOT CLICKED", Toast.LENGTH_SHORT).show();
                // TODO implement this
               // launchLocationService();
            case R.id.action_credits:
                Toast.makeText(this, "CREDITS CLICKED", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
