package com.joelcamargojr.androidhub.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.joelcamargojr.androidhub.PodcastAudioService;
import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.Utils.ExoPlayerUtils;
import com.joelcamargojr.androidhub.Utils.MetaDataUtils;
import com.joelcamargojr.androidhub.databinding.PlayerPodcastBinding;
import com.joelcamargojr.androidhub.model.Episode;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import timber.log.Timber;

public class EpisodePlayerActivity extends AppCompatActivity {

    PlayerPodcastBinding binding;
    public static Episode currentEpisode;
    MediaMetadataCompat mediaMetadataCompat;
    SimpleExoPlayer player;
    public static String mLastEpisodePlayed;
    public static boolean mIsSameEpisode = false;
    public static boolean mServiceWasStarted;
    public static long mPlayerPosition;
    public static boolean mPlayWhenReady;
    public static int mPlayerWindow;
    public boolean mStartedFromNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("ONCREATE STARTING");

        binding = DataBindingUtil.setContentView(this, R.layout.player_podcast);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Gets bundle that should have our Podcast data
        Bundle bundle = getIntent().getBundleExtra("bundle");

        // Instantiates Episode object from Bundle
        currentEpisode = Parcels.unwrap(bundle.getParcelable("episode"));

        // Sets mediaUrl from Episode object to setup the Player
        ExoPlayerUtils.setMediaString(currentEpisode.audioUrl);

        // Booleans that help us figure out whether we create new data for the player or keep the same data
        mStartedFromNotification = bundle.getBoolean("startedFromNotification", false);

        // Checks if activity is starting from a notification PendingIntent or fresh startup
        if (!mStartedFromNotification) {
            Timber.d("NOT started from a notification");

            if (savedInstanceState == null) {
                // No state to restore. Fresh setup.
                Timber.d("onsaved NULL. Fresh setup");
                initialPlayerSetup();
                setupActivity(currentEpisode);

            } else {

                // Gets last played episode
                mLastEpisodePlayed = savedInstanceState.getString("lastEpisodePlayed", null);
                if (mLastEpisodePlayed != null && mLastEpisodePlayed.equals(currentEpisode.title)) {
                    mIsSameEpisode = true;
                }

                if (mIsSameEpisode) {
                    Timber.d("EPISODES MATCH. Restore state");
                    // Initialize variables to restore previous state
                    mPlayWhenReady = savedInstanceState.getBoolean("playWhenReady");
                    mPlayerWindow = savedInstanceState.getInt("playerWindow");
                    mPlayerPosition = savedInstanceState.getLong("playerPosition");
                    mServiceWasStarted = savedInstanceState.getBoolean("wasServiceStarted");
                    logState();

                    initialPlayerSetup();
                    setupActivity(currentEpisode);
                    player.setPlayWhenReady(mPlayWhenReady);
                    player.seekTo(mPlayerWindow, mPlayerPosition);
                }
            }
        } else {
            currentEpisode = Parcels.unwrap(bundle.getParcelable("episode"));
            setupActivity(currentEpisode);
        }

        mediaMetadataCompat = MetaDataUtils.setMetaDataForMediaSession(this, currentEpisode);
    }

    private void initialPlayerSetup() {
        player = ExoPlayerUtils.createExoPlayer(this);
        ExoPlayerUtils.preparePlayer(this, player);
        player.addListener(new BasicPlayerEventListener());
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

            if (!mStartedFromNotification) {
                Timber.d("NOT started from notification. Setting new player");
                binding.playerControlView.setPlayer(player);
            } else {
                Timber.d("Started from notification. Getting Service player");
                player = PodcastAudioService.getExistingPlayer();
                binding.playerControlView.setPlayer(player);
            }

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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("ON START");
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

//        if (!mServiceWasStarted) {
//            startService(new Intent(this, PodcastAudioService.class));
//            mServiceWasStarted = true;
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.d("ONPAUSE");
        savePlayerState();
        logState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        logState();

        outState.putBoolean("wasServiceStarted", mServiceWasStarted);
        outState.putLong("playerPosition", mPlayerPosition);
        outState.putInt("playerWindow", mPlayerWindow);
        outState.putString("lastEpisodePlayed", mLastEpisodePlayed);
        outState.putBoolean("playWhenReady", mPlayWhenReady);

        super.onSaveInstanceState(outState);
    }

    // Saves player state for device rotation
    public void savePlayerState() {
        mLastEpisodePlayed = currentEpisode.title;
        mPlayerPosition = player.getCurrentPosition();
        mPlayerWindow = player.getCurrentWindowIndex();
        mPlayWhenReady = player.getPlayWhenReady();
    }

    // Logs player state variables
    public void logState() {
        Timber.d("wasServiceStarted= %s", mServiceWasStarted);
        Timber.d("playerPosition= %s", mPlayerPosition);
        Timber.d("playerWindow= %s", mPlayerWindow);
        Timber.d("lastEpisodePlayed= %s", mLastEpisodePlayed);
        Timber.d("playWhenReady= %s", mPlayWhenReady);
    }

    // Listener just to start the service. Then service will take over running the player
    private class BasicPlayerEventListener implements Player.EventListener {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_READY && playWhenReady) {
                startService(new Intent(getApplicationContext(), PodcastAudioService.class));
                mServiceWasStarted = true;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }
}
