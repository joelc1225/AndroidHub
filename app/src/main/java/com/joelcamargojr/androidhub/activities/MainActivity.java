package com.joelcamargojr.androidhub.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.Utils.InjectorUtils;
import com.joelcamargojr.androidhub.databinding.ActivityMainBinding;
import com.joelcamargojr.androidhub.model.Podcast;
import com.joelcamargojr.androidhub.recyclerview.MainRecyclerviewAdapter;
import com.joelcamargojr.androidhub.viewModels.MainActivityViewModel;
import com.joelcamargojr.androidhub.viewModels.MainViewModelFactory;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    MainActivityViewModel mViewModel;
    MutableLiveData<Podcast> mPodcast;
    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create data binding instance for views
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // set up toolbar
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        mBinding.mainProgressbar.setVisibility(View.VISIBLE);

        // Creates ViewModel
        MainViewModelFactory factory = new MainViewModelFactory(InjectorUtils.provideRepository(this));
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        // Instantiates Podcast data needed to inflate UI with API call
        mPodcast = mViewModel.getPodcastData();

        // Observes the data when retrieved from the API call
        mPodcast.observe(this, new Observer<Podcast>() {
            @Override
            public void onChanged(@Nullable final Podcast podcast) {

                mBinding.mainProgressbar.setVisibility(View.INVISIBLE);

                if (podcast != null) {
                    bindUi();
                } else {
                    mBinding.retryButton.setVisibility(View.VISIBLE);
                    mBinding.retryButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Retrying Request", Toast.LENGTH_LONG).show();
                            //TODO retry request somehow
                            mPodcast = mViewModel.getPodcastData();
                        }
                    });
                }
            }
        });

        // If started from widget with no previously saved Episode data, will show this toast
        String widgetAction = getIntent().getAction();
        if (Objects.equals(widgetAction, getString(R.string.widgetToastAction))) {
            Toast.makeText(this, R.string.widgetToastMessage, Toast.LENGTH_SHORT).show();
        }
    }



    private void bindUi() {
        mViewModel.setEpisodesList();
        RecyclerView recyclerView = mBinding.recyListenFrag;
        MainRecyclerviewAdapter adapter =
                new MainRecyclerviewAdapter(mViewModel.getEpisodesList(), getApplicationContext());
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
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
            case R.id.action_credits:
                Toast.makeText(this, "CREDITS CLICKED", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
