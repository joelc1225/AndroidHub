package com.joelcamargojr.androidhub.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.databinding.PodcastDetailBinding;
import com.joelcamargojr.androidhub.model.Podcast;
import com.joelcamargojr.androidhub.recyclerview.EpisodeRecyclerviewAdapter;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import timber.log.Timber;

public class PodcastDetailActivity extends AppCompatActivity {

    PodcastDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.podcast_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Gets bundle that should have our Podcast data
        Bundle bundle = getIntent().getBundleExtra("bundle");

        if (bundle != null) {
            Podcast podcast = Parcels.unwrap(bundle.getParcelable("podcast"));
            Timber.d(podcast.image);
            Picasso.get()
                    .load(R.drawable.fragmented_image)
                    .into(binding.podcastImageview);
            binding.podcastDescriptionTextview.setText(podcast.description);
            binding.podcastTitleTextView.setText(podcast.title);
            RecyclerView episodesRV = binding.episodesRV;
            EpisodeRecyclerviewAdapter adapter = new EpisodeRecyclerviewAdapter(this, podcast.episodeArrayList);
            episodesRV.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            episodesRV.setLayoutManager(layoutManager);

        }

    }
}
