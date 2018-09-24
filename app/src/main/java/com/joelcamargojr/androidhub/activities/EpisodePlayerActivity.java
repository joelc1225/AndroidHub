package com.joelcamargojr.androidhub.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.Utils.NotificationUtils;
import com.joelcamargojr.androidhub.databinding.PlayerPodcastBinding;
import com.joelcamargojr.androidhub.model.Episode;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import timber.log.Timber;

import static com.joelcamargojr.androidhub.Utils.ExoPlayerUtils.createExoPlayer;
import static com.joelcamargojr.androidhub.Utils.ExoPlayerUtils.preparePlayer;

public class EpisodePlayerActivity extends AppCompatActivity {

    static NotificationUtils notificationUtils;
    PlayerPodcastBinding binding;
    SimpleExoPlayer simpleExoPlayer;
    static Episode currentEpisode;
    final String TAG = EpisodePlayerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("ONCREATE STARTING");
        binding = DataBindingUtil.setContentView(this, R.layout.player_podcast);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Gives us access to the methods in our notificationUtils class
        notificationUtils = new NotificationUtils(this);

        // Gets bundle that should have our Podcast data and setup views/player
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            Timber.d("ONCREATE BUNDLE IS NOT NULL");
            // Instantiates Episode object from Bundle
            currentEpisode = Parcels.unwrap(bundle.getParcelable("episode"));
            setupActivity(currentEpisode);
        } else {
            Timber.d("ONCREATE BUNDLE IS NULL");
        }
    }

    private void setupActivity(Episode episode) {
        Timber.d("STARTING ACTIVITY SETUP");
        if (episode != null) {
            Picasso.get()
                    .load(R.drawable.fragmented_image)
                    .into(binding.imageViewPodcast);

            binding.playerPodcastDescripTv.setText(Html.fromHtml(currentEpisode.description));
            binding.playerPodcastDescripTv.setMovementMethod(LinkMovementMethod.getInstance());
            binding.playerPodcastTitleTv.setText(currentEpisode.title);

            // sets player and prepares with episode's audio
            if (simpleExoPlayer == null) {
                simpleExoPlayer = createExoPlayer(this);
                binding.playerControlView.setPlayer(simpleExoPlayer);
            }

            preparePlayer(this, currentEpisode.audioUrl, simpleExoPlayer);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("ONDESTROY CALLED");
    }

    // Will setup the activity and views when notification is clicked and activity was destroyed
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Timber.d("ON NEW INTENT CALLED HERE");
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("ON RESUME CALLED HERE");
        if (currentEpisode == null) {
            Timber.d("SETTING UP FROM ONRESUME????");
            currentEpisode = getIntent().getExtras().getParcelable("episode");
            setupActivity(currentEpisode);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
