package com.joelcamargojr.androidhub.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.Utils.InjectorUtils;
import com.joelcamargojr.androidhub.databinding.ActivityFavoritesBinding;
import com.joelcamargojr.androidhub.recyclerview.FavoritesRecyclerviewAdapter;
import com.joelcamargojr.androidhub.viewModels.FavoritesActivityViewModel;
import com.joelcamargojr.androidhub.viewModels.FavoritesViewModelFactory;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesActivity extends AppCompatActivity {

    ActivityFavoritesBinding mBinding;
    FavoritesActivityViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create data binding instance for views
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorites);

        // set up toolbar
        Toolbar toolbar = mBinding.toolbarFaves;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mBinding.favesProgressbar.setVisibility(View.VISIBLE);

        // Creates ViewModel
        FavoritesViewModelFactory factory = new FavoritesViewModelFactory(InjectorUtils.provideRepository(this));
        mViewModel = ViewModelProviders.of(this, factory).get(FavoritesActivityViewModel.class);

        // Gets/Observes Podcast data needed to inflate UI with API call
        mViewModel.getFavoriteEpisodesList().observe(this, episodes -> {

            mBinding.favesProgressbar.setVisibility(View.INVISIBLE);

            if (episodes != null && episodes.size() > 0) {
                bindUi();
            } else {
                mBinding.noFavesTV.setVisibility(View.VISIBLE);
            }
        });
    }

    private void bindUi() {
        RecyclerView recyclerView = mBinding.recyFaves;
        FavoritesRecyclerviewAdapter adapter =
                new FavoritesRecyclerviewAdapter(mViewModel.getFavoriteEpisodesList(), getApplicationContext());
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
