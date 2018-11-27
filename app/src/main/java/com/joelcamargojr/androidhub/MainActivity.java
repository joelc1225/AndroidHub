package com.joelcamargojr.androidhub;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.joelcamargojr.androidhub.Utils.InjectorUtils;
import com.joelcamargojr.androidhub.databinding.ActivityMainBinding;
import com.joelcamargojr.androidhub.model.Podcast;
import com.joelcamargojr.androidhub.recyclerview.MainRecyclerviewAdapter;
import com.joelcamargojr.androidhub.viewModels.MainActivityViewModel;
import com.joelcamargojr.androidhub.viewModels.MainViewModelFactory;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    MainActivityViewModel mViewModel;
    MutableLiveData<Podcast> mPodcast;

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

        MainViewModelFactory factory = new MainViewModelFactory(InjectorUtils.provideRepository(this));
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);
        mPodcast = mViewModel.getPodcastData();
        mPodcast.observe(this, new Observer<Podcast>() {
            @Override
            public void onChanged(@Nullable final Podcast podcast) {

                binding.mainProgressbar.setVisibility(View.INVISIBLE);

                if (podcast != null) {
                    bindUi();
                } else {
                    binding.retryButton.setVisibility(View.VISIBLE);
                    binding.retryButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Retrying Request", Toast.LENGTH_LONG).show();
                            //TODO retry request somehow
                            mPodcast = mViewModel.getPodcastData();
                        }
                    });
                }
            }

            private void bindUi() {
                mViewModel.setEpisodesList();
                RecyclerView recyclerView = binding.recyListenFrag;
                MainRecyclerviewAdapter adapter =
                        new MainRecyclerviewAdapter(mViewModel.getEpisodesList(), getApplicationContext());
                recyclerView.setAdapter(adapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
            }
        });

        String widgetAction = getIntent().getAction();
        if (Objects.equals(widgetAction, getString(R.string.widgetToastAction))) {
            Toast.makeText(this, R.string.widgetToastMessage, Toast.LENGTH_SHORT).show();
        }
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
                break;
            case R.id.action_credits:
                Toast.makeText(this, "CREDITS CLICKED", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
