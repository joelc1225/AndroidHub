package com.joelcamargojr.androidhub.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;

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
import com.joelcamargojr.androidhub.widget.WidgetProvider;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import timber.log.Timber;

public class EpisodePlayerActivity extends AppCompatActivity {

    PlayerPodcastBinding binding;
    public static Episode currentEpisode;
    MediaMetadataCompat mediaMetadataCompat;
    public static SimpleExoPlayer player;
    public static String mLastEpisodePlayed;
    public static String mPrefsLastEpisodePlayed;
    public static boolean mIsSameEpisode;
    public static boolean mConfigChange;
    public static boolean mServiceWasStarted;
    public static boolean mIsFreshSetup;
    public static boolean mIsCurrentlyPlayingThisEpisode;
    public static boolean mStartedFromWidget;
    public static long mPlayerPosition;
    public static boolean mPlayWhenReady;
    public static int mPlayerWindow;
    public boolean mStartedFromNotification;
    private SharedPreferences mSharedPreferences;
    SharedPreferences.Editor sharedPrefsEditor;
    private static String audioUrlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.player_podcast);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mSharedPreferences = this.getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        binding.episodePlayerProgressbar.setVisibility(View.VISIBLE);

        getBundleAndExtractEpisodeData();

        precheckForActivitySetup(savedInstanceState);

        setupActivity();
    }

    // Gets Episode data from intent and extracts out necessary data
    private void getBundleAndExtractEpisodeData() {
        // Gets bundle that should have our Podcast data if the activity started from within our app
        Bundle bundle = getIntent().getBundleExtra(getString(R.string.bundle_key));

        // Instantiates Episode object from Bundle, if the data exists
        if (bundle != null) {
            if (bundle.containsKey(getString(R.string.episode_key))) {
                Timber.d("BUNDLE CONTAINS EPISODE!");
                // Gets episode data passed in from recyclerview
                currentEpisode = Parcels.unwrap(bundle.getParcelable(getString(R.string.episode_key)));

                // Boolean that help us figure out whether we create new data for the player or keep the same data
                mStartedFromNotification = bundle.getBoolean("startedFromNotification", false);

                if (Objects.equals(getIntent().getAction(), WidgetProvider.ACTION_WIDGET_CLICK)) {
                    Timber.d("BUNDLE HAS WIDGET ACTION!");
                    mStartedFromNotification = false;
                    mStartedFromWidget = true;
                } else {
                    mStartedFromWidget = false;
                }
            }

            audioUrlString = mSharedPreferences.getString(getString(R.string.last_played_audioUrl_key), null);
        }
    }

    // Checks state of app and variables to determine how we should setup activity
    private void precheckForActivitySetup(Bundle savedInstanceState) {
        Timber.d("PRECHECK STARTING");
        // Resets booleans to defaults before checking state of
        mConfigChange = false;
        mIsSameEpisode = false;
        mIsFreshSetup = false;
        mIsCurrentlyPlayingThisEpisode = false;

        // Resets global player position for startup
        mPlayerPosition = 0;

        // Gets title of the episode that was last playing from SharedPrefs
        mPrefsLastEpisodePlayed =
                mSharedPreferences.getString(getString(R.string.last_played_episode_key), null);

        // Checks sharedPrefs if service is currently running
        mServiceWasStarted = mSharedPreferences.getBoolean(getString(R.string.service_was_started_key), false);
        Timber.d("mServiceWasStarted = %s", mServiceWasStarted);

        // If there is state saved. Initialize everything needed to restore state on orientation change
        if (savedInstanceState != null) {
            // Gets last played episode from onSavedInstanceState for config change setup
            mLastEpisodePlayed = savedInstanceState.getString(getString(R.string.lastEpisodePlayed_key), null);

            // Checks if lastEpisodePlayed and current episode data are the same
            // If true, we are in a config change scenario
            if (mLastEpisodePlayed != null && mLastEpisodePlayed.equals(currentEpisode.title)) {
                mConfigChange = true;
            }

            // Checks SharedPrefs if the currently selected episode was also
            // the last one playing or currently playing
            if (mPrefsLastEpisodePlayed != null) {
                if (currentEpisode.title.equals(mPrefsLastEpisodePlayed)) {
                    mIsSameEpisode = true;

                    // Gets the last known position on the current audioFile
                    mPlayerPosition = mSharedPreferences.getLong(getString(R.string.last_played_position_key), 0);
                }
            }

            // Sets all variables from savedInstanceState to restore state of player
            mPlayWhenReady = savedInstanceState.getBoolean("playWhenReady");
            mPlayerWindow = savedInstanceState.getInt("playerWindow");
            mPlayerPosition = savedInstanceState.getLong("playerPosition");
            mServiceWasStarted = savedInstanceState.getBoolean("wasServiceStarted");
        } else {
            // if no saved state, determine if the most recently played episode is the same as the
            // currently selected episode
            if (mPrefsLastEpisodePlayed != null && currentEpisode != null) {
                if (mPrefsLastEpisodePlayed.equals(currentEpisode.title)) {
                    mIsSameEpisode = true;
                    mPlayerPosition = mSharedPreferences.getLong(getString(R.string.last_played_position_key), 0);
                    Timber.d("Player Position is: %s", mPlayerPosition);

                }
            } else {
                Timber.e("SharedPrefs last episode or currentEpisode NOT the same");
            }
        }

        // Evaluating these expressions means its a whole new episode and we can start fresh
        if (!mConfigChange && !mIsSameEpisode) {
            mIsFreshSetup = true;
        }

        // If episode is the same and service is running, evaluates sets corresponding boolean to true
        if (mIsSameEpisode && mServiceWasStarted) {
            mIsCurrentlyPlayingThisEpisode = true;
        }

        if (mStartedFromWidget) {
            mPlayerPosition = mSharedPreferences.getLong(getString(R.string.last_played_position_key), 0);
            Timber.d("Player Position is: %s", mPlayerPosition);
        }
    }

    // After determining startup conditions, sets up activity accordingly
    private void setupActivity() {
        // Checks if activity is starting from a notification PendingIntent or fresh startup
        if (!mStartedFromNotification) {
            // Checks if the episode data is the same before setup
            if (mIsFreshSetup || mStartedFromWidget) {
                setupPlayer();
                setupActivityLayout(currentEpisode);
            } else if (mIsCurrentlyPlayingThisEpisode) {
                // Already playing the same episode. Just need to set up views
                setupActivityLayout(currentEpisode);
            } else {
                // Handles orientation changes or setting up last played episode
                setupPlayer();
                setupActivityLayout(currentEpisode);
                player.setPlayWhenReady(mPlayWhenReady);
                player.seekTo(mPlayerWindow, mPlayerPosition);
            }
        } else {
            // Started from notification click. Player is already setup. Just need to inflate views
            setupActivityLayout(currentEpisode);
        }

        mediaMetadataCompat = MetaDataUtils.setMetaDataForMediaSession(this, currentEpisode);
    }

    private void setupPlayer() {
        Timber.d("PLAYER SETUP");

        // If widget was clicked while already playing the episode, returns active player
        // else, builds and prepares new player
        if (mStartedFromWidget && mServiceWasStarted) {
            Timber.d("GETTING PLAYER FROM STARTED SERVICE");
            player = PodcastAudioService.getExistingPlayer();
        } else {
            player = ExoPlayerUtils.createExoPlayer(this);
            if (currentEpisode != null) {
                Timber.d("CURRENT EPISODE NOT NULL. USING IT TO PREPARE PLAYER");
                ExoPlayerUtils.preparePlayer(this, player, currentEpisode.audioUrl);
            } else {
                Timber.d("CURRENT EPISODE IS NULL. PREPARING FROM SHARED PREFS");
                ExoPlayerUtils.preparePlayer(this, player, audioUrlString);
                player.seekTo(mPlayerPosition);
            }

            player.addListener(new BasicPlayerEventListener());

            if (mStartedFromWidget || mIsSameEpisode) {
                Timber.d("INSIDE STARTEDFROMWIDGET 'if' block to set position");
                player.seekTo(mPlayerPosition);
            }
        }

        logPlayerState();
    }

    private void setupActivityLayout(Episode episode) {
        if (episode != null) {
            Picasso.get()
                    .load(R.drawable.fragmented_image)
                    .into(binding.imageViewPodcast);

            binding.playerPodcastDescripTv.setText(Html.fromHtml(currentEpisode.description));
            binding.playerPodcastDescripTv.setMovementMethod(LinkMovementMethod.getInstance());
            binding.playerPodcastTitleTv.setText(currentEpisode.title);

            if (mServiceWasStarted) {
                // Service is running. get existing player and set on playerView
                player = PodcastAudioService.getExistingPlayer();
                binding.playerControlView.setPlayer(player);
            } else {
                // service not running. use newly created player
                binding.playerControlView.setPlayer(player);
            }
        } else {
            // Started from widget or notification, so either click gets data from last played episode
            String episode_description =
                    mSharedPreferences.getString(getString(R.string.last_played_episode_description_key), "Empty description");
            String episode_title =
                    mSharedPreferences.getString(getString(R.string.last_played_episode_key), "Default title");

            Picasso.get()
                    .load(R.drawable.fragmented_image)
                    .into(binding.imageViewPodcast);

            binding.playerPodcastDescripTv.setText(Html.fromHtml(episode_description));
            binding.playerPodcastDescripTv.setMovementMethod(LinkMovementMethod.getInstance());
            binding.playerPodcastTitleTv.setText(episode_title);

            if (mServiceWasStarted) {
                // Service is running. get existing player
                player = PodcastAudioService.getExistingPlayer();
                binding.playerControlView.setPlayer(player);
            } else {
                // service not running. use newly created player
                binding.playerControlView.setPlayer(player);
            }
        }

        // todo doesnt do anything
        binding.episodePlayerProgressbar.setVisibility(View.INVISIBLE);
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
    protected void onStart() {
        super.onStart();
        Timber.d("ON START");
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePlayerState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("wasServiceStarted", mServiceWasStarted);
        outState.putLong("playerPosition", mPlayerPosition);
        outState.putInt("playerWindow", mPlayerWindow);
        outState.putString("lastEpisodePlayed", mLastEpisodePlayed);
        outState.putBoolean("playWhenReady", mPlayWhenReady);

        super.onSaveInstanceState(outState);
    }

    // Saves player state for device rotation
    public void savePlayerState() {

        if (player != null) {
            mLastEpisodePlayed = currentEpisode.title;
            mPlayerPosition = player.getCurrentPosition();
            mPlayerWindow = player.getCurrentWindowIndex();
            mPlayWhenReady = player.getPlayWhenReady();
        } else {
            Timber.d("PLAYER NULL. CANNOT SAVE STATE");
        }

    }

    // for debugging purposes
    public void logPlayerState() {
        Timber.d("CURRENT EPISODE TITLE: %s", currentEpisode.title);
        Timber.d("PLAYER POSITION: %s", mPlayerPosition);
        Timber.d("PLAYER PLAYWHENREADY: %s", mPlayWhenReady);
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
                Timber.d("PRESSED ACTIVITY PLAY BUTTON");
                // Saves last played episode data to SharedPreferences in case we need to rebuild
                // the player\activity later
                saveEpisodeToSharedPrefs();

                // Updates the widget UI info to display most recent episode played
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                WidgetProvider widgetProvider = new WidgetProvider();
                RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_layout);
                ComponentName myWidget = new ComponentName(getApplicationContext(), WidgetProvider.class);
                remoteViews.setTextViewText(R.id.appwidget_text, currentEpisode.title);
                appWidgetManager.updateAppWidget(myWidget, remoteViews);

                // Calls widgets onUpdate method to update the widgets PendingIntent
                int[] widgetIds = appWidgetManager.getAppWidgetIds(myWidget);
                widgetProvider.onUpdate(getApplicationContext(), appWidgetManager, widgetIds);

                Timber.d("mServiceStarted: %s", mServiceWasStarted);

                // Starts background  audio service
                if (!mServiceWasStarted) {
                    Timber.d("SERVICE NOT STARTED YET. LAUNCHING SERVICE INTENT FROM LISTENER!");

                    // Updates the boolean value in sharedPrefs to show that the service was launched
                    sharedPrefsEditor.putBoolean(getString(R.string.service_was_started_key), true);
                    sharedPrefsEditor.apply();

                    mServiceWasStarted = mSharedPreferences.getBoolean(getString(R.string.service_was_started_key), false);
                    Timber.d("TESTboolean in player listener: %s", mServiceWasStarted);

                    startService(new Intent(getApplicationContext(), PodcastAudioService.class)
                            .putExtra(getString(R.string.last_played_position_key), mPlayerPosition)
                            .putExtra(getString(R.string.started_from_widget_key), mStartedFromWidget));
                }

                Timber.d("2nd TESTboolean in player listener: %s", mServiceWasStarted);
            }
            else {
                // User has either clicked pause, rewind, or fastforward
                Timber.d("SEEKED, PAUSED, FF, or RW???");
                // updates the episode data to SharedPreferences in case we need to rebuild
                // the player\activity later
                if (player.getCurrentPosition() != 0) {
                    saveEpisodeToSharedPrefs();
                }

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

    private void saveEpisodeToSharedPrefs() {
        sharedPrefsEditor = mSharedPreferences.edit();
        sharedPrefsEditor.putString(getString(R.string.last_played_episode_key), currentEpisode.title);
        sharedPrefsEditor.putString(getString(R.string.last_played_audioUrl_key), currentEpisode.audioUrl);
        sharedPrefsEditor.putString(getString(R.string.last_played_episode_description_key), currentEpisode.description);
        sharedPrefsEditor.putInt(getString(R.string.last_played_audio_length_key), currentEpisode.audio_length);
        sharedPrefsEditor.putString(getString(R.string.last_played_id_key), currentEpisode.id);
        sharedPrefsEditor.putLong(getString(R.string.last_played_date_key), currentEpisode.date);
        sharedPrefsEditor.putString(getString(R.string.last_played_listenNotesUrl_key), currentEpisode.listennotes_url);
        sharedPrefsEditor.apply();
    }

    // static method that gets called from the service into our player activity
    // and avoids re-creating a player
    public static SimpleExoPlayer getExistingPlayer() {
        return player;
    }
}