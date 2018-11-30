package com.joelcamargojr.androidhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.Utils.InjectorUtils;
import com.joelcamargojr.androidhub.databinding.ActivityMainBinding;
import com.joelcamargojr.androidhub.recyclerview.MainRecyclerviewAdapter;
import com.joelcamargojr.androidhub.viewModels.MainActivityViewModel;
import com.joelcamargojr.androidhub.viewModels.MainViewModelFactory;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel mViewModel;
    ActivityMainBinding mBinding;
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        // create data binding instance for views
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // set up toolbar
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        mBinding.mainProgressbar.setVisibility(View.VISIBLE);

        // Instantiate Firebase Analytics (one instance per app launch)
        mFirebaseAnalytics = InjectorUtils.provideFirebaseAnalytics(this);

        // Creates ViewModel
        MainViewModelFactory factory = new MainViewModelFactory(InjectorUtils.provideRepository(this));
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        // Gets/Observes Podcast data needed to inflate UI with API call
        mViewModel.getPodcastData().observe(this, podcast -> {

            mBinding.mainProgressbar.setVisibility(View.INVISIBLE);

            if (podcast != null) {
                bindUi();
            } else {
                mBinding.retryButton.setVisibility(View.VISIBLE);
                mBinding.retryButton.setOnClickListener(v -> {

                    Toast.makeText(getApplicationContext(), R.string.retrying_request_toast, Toast.LENGTH_LONG).show();
                    mViewModel.retryApiCall().observe(MainActivity.this, podcast1 -> {
                        if (podcast1 != null) {
                            mBinding.retryButton.setVisibility(View.INVISIBLE);
                            bindUi();
                        }
                    });
                });
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
        RecyclerView recyclerView = mBinding.recyMain;
        MainRecyclerviewAdapter adapter =
                new MainRecyclerviewAdapter(mViewModel.getEpisodesList(), getApplicationContext());
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        // Logs title of first episode in Episode list into Firebase Analytics
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.QUANTITY, mViewModel.getEpisodesList().getValue().get(0).title);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
            case R.id.action_favorites:
                Intent startFavoritesIntent = new Intent(this, FavoritesActivity.class);
                startActivity(startFavoritesIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
