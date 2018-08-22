package com.joelcamargojr.androidhub.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.databinding.PlayerPodcastBinding;
import com.joelcamargojr.androidhub.model.Episode;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import timber.log.Timber;

import static com.joelcamargojr.androidhub.Utils.ExoPlayerUtils.createExoPlayer;
import static com.joelcamargojr.androidhub.Utils.ExoPlayerUtils.getDefaultListener;
import static com.joelcamargojr.androidhub.Utils.ExoPlayerUtils.preparePlayer;

public class EpisodePlayerActivity extends AppCompatActivity {

    PlayerPodcastBinding binding;
    SimpleExoPlayer simpleExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.player_podcast);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Gets bundle that should have our Podcast data
        Bundle bundle = getIntent().getBundleExtra("bundle");

        if (bundle != null) {
            Episode episode = Parcels.unwrap(bundle.getParcelable("episode"));
            Picasso.get()
                    .load(R.drawable.fragmented_image)
                    .into(binding.imageViewPodcast);

            binding.playerPodcastDescripTv.setText(Html.fromHtml(episode.description));
            binding.playerPodcastDescripTv.setMovementMethod(LinkMovementMethod.getInstance());
            binding.playerPodcastTitleTv.setText(episode.title);

            // sets player and prepares with episode's audio
            if (simpleExoPlayer == null) {
                Timber.d("EXOPLAYER IS NULL!!!");
                simpleExoPlayer = createExoPlayer(this);
                simpleExoPlayer.addListener(getDefaultListener());
                binding.playerControlView.setPlayer(simpleExoPlayer);
            }

            preparePlayer(this, episode.audioUrl, simpleExoPlayer);
        }
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
