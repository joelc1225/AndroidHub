package com.joelcamargojr.androidhub;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.joelcamargojr.androidhub.Utils.ExoPlayerUtils;
import com.joelcamargojr.androidhub.Utils.MetaDataUtils;
import com.joelcamargojr.androidhub.activities.EpisodePlayerActivity;
import com.joelcamargojr.androidhub.activities.MainActivity;

import org.parceler.Parcels;

import timber.log.Timber;

public class PodcastAudioService extends Service {

    private final String TAG = PodcastAudioService.class.getSimpleName();
    private static MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder stateBuilder;
    public static SimpleExoPlayer player;
    private AudioManager mAudioManager;
    private MyFocusListener mMyFocusListener;
    boolean mAudioFocusGranted = false;
    private AudioFocusRequest mFocusRequest;
    final int NOTIFICATION_ID = 1100;
    PlayerNotificationManager playerNotificationManager;
    MediaMetadataCompat metadataCompat;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    String mAudioUrlString;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;

        Timber.d("SERVICE ONCREATE RUNNING");

        mSharedPreferences = context.getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        if (mSharedPreferences.contains(getString(R.string.last_played_audioUrl_key))) {

            mAudioUrlString =
                    mSharedPreferences.getString(getString(R.string.last_played_audioUrl_key), null);
        }


        // Gets system audio service and creates audio focus request for future reference
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Instantiate everything required for focus request
        mMyFocusListener = new MyFocusListener();
        initFocusRequest();

        // inits mediaSession and sets initial state
        initMediaSession();

        // Creates the service exoplayer
        initPlayer();

        // Creates Notification manager
        initPlayerNotificationManager(context);

    }

    // Method that creates the player's notification and manages changes
    private void initPlayerNotificationManager(final Context context) {

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(this,
                context.getString(R.string.channel_id_player),
                R.string.channel_name_player, NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {

                    @Override
                    public String getCurrentContentTitle(Player player) {
                        metadataCompat = MetaDataUtils.getMediaMetadata();
                        if (metadataCompat == null) {
                            return getString(R.string.label_fragmented_podcast);
                        } else {
                            return metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
                        }
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {

                        Intent mainActivityIntent = new Intent(context, MainActivity.class);
                        Intent episodeActivityIntent = new Intent(context, EpisodePlayerActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("episode", Parcels.wrap(EpisodePlayerActivity.currentEpisode));
                        bundle.putBoolean("startedFromNotification", true);
                        episodeActivityIntent.putExtra("bundle", bundle);

                        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                                .addNextIntent(mainActivityIntent)
                                .addNextIntentWithParentStack(episodeActivityIntent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//                        return PendingIntent.getActivity(context, 0, episodeActivityIntent,
//                                PendingIntent.FLAG_UPDATE_CURRENT);

                        return pendingIntent;
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        if (metadataCompat == null) {
                            return mSharedPreferences.getString(getString(R.string.last_played_episode_key), null);
                        } else {
                            return metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
                        }
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        if (metadataCompat == null) {
                            return BitmapFactory.decodeResource(context.getResources(), R.drawable.fragmented_image);
                        } else {
                            return metadataCompat.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
                        }
                    }
                });

        playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                Timber.d("ON NOTIFICATION CANCELED");
                player = null;
                stopSelf();
            }
        });
        playerNotificationManager.setUseNavigationActions(false);
        playerNotificationManager.setFastForwardIncrementMs(10000);
        playerNotificationManager.setRewindIncrementMs(10000);
        playerNotificationManager.setStopAction(null);
        playerNotificationManager.setSmallIcon(R.drawable.exo_notification_small_icon);
        playerNotificationManager.setColorized(true);
        playerNotificationManager.setMediaSessionToken(mediaSessionCompat.getSessionToken());
        playerNotificationManager.setPriority(NotificationCompat. PRIORITY_DEFAULT);
        playerNotificationManager.setPlayer(player);
        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSessionCompat);
        mediaSessionConnector.setPlayer(player, null);

    }

    private void initPlayer() {
        // gets prepared player from EpisodePlayerActivity
        player = EpisodePlayerActivity.getExistingPlayer();
        player.addListener(new MyExoPlayerEventListener());
    }

    // Helper method to request audio focus before playing audio
    private void initFocusRequest() {
        AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(mPlaybackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(mMyFocusListener)
                    .build();
        }
    }

    // Helper method that creates the mediaSession
    private void initMediaSession() {
        // Creates media session for podcast player
        mediaSessionCompat = new MediaSessionCompat(this, TAG);

        // Enables callbacks from media buttons/transport controls
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setMediaButtonReceiver(null);

        // Sets initial state and available actions
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        mediaSessionCompat.setPlaybackState(stateBuilder.build());

        mediaSessionCompat.setCallback(new MySessionCallbacks());
        mediaSessionCompat.setMetadata(MetaDataUtils.getMediaMetadata());
        mediaSessionCompat.setActive(true);
    }

    // Where all external clients control mediaSession
    private class MySessionCallbacks extends MediaSessionCompat.Callback {

        IntentFilter intentFilter =
                new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        BecomingNoisyReceiver becomingNoisyReceiver = new BecomingNoisyReceiver();

        @Override
        public void onPlay() {
            super.onPlay();
            if (mMyFocusListener.requestAudioFocus()) {
                player.setPlayWhenReady(true);
                registerReceiver(becomingNoisyReceiver, intentFilter);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            player.setPlayWhenReady(false);
            unregisterReceiver(becomingNoisyReceiver);
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
            // Fast forward 10 seconds
            player.seekTo(player.getCurrentPosition() + 10000);
        }

        @Override
        public void onRewind() {
            super.onRewind();
            // Rewind 10 seconds if more than 10 seconds have elapsed
            if (player.getCurrentPosition() >= 10000) {
                player.seekTo(player.getCurrentPosition() - 10000);
            } else {
                player.seekTo(0);
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            // Fast forward 10 seconds
            player.seekTo(player.getCurrentPosition() + 10000);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            // Rewind 10 seconds if more than 10 seconds have elapsed
            if (player.getCurrentPosition() >= 10000) {
                player.seekTo(player.getCurrentPosition() - 10000);
            } else {
                player.seekTo(0);
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            mMyFocusListener.abandonAudioFocus();
            unregisterReceiver(becomingNoisyReceiver);
            mediaSessionCompat.setActive(false);
            player.setPlayWhenReady(false);
            releasePlayer();
        }


    }

    // Broadcast Receiver that deals with unplugged headphones or BT device disconnect
    public class BecomingNoisyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // if audio headset or speaker is disconnected, will pause audio
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                Timber.d("TRIGGERED ACTION BECOMING NOISY");
                player.setPlayWhenReady(false);
                stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        player.getCurrentPosition(),
                        1f);
                mediaSessionCompat.setPlaybackState(stateBuilder.build());
            }

        }
    }

    // Event listener for ExoPlayer
    private class MyExoPlayerEventListener implements Player.EventListener {
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

            // implement state changes and audio focus changes on events
            if ((playbackState == Player.STATE_READY) && playWhenReady) {
                // Request focus before playing
                mAudioFocusGranted = mMyFocusListener.requestAudioFocus();
                if (mAudioFocusGranted) {
                    // Playing
                    stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                            player.getCurrentPosition(),
                            1f);
                }
            } else if (playbackState == Player.STATE_READY) {
                // Paused
                mMyFocusListener.abandonAudioFocus();
                stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        player.getCurrentPosition(),
                        1f);
            } else if (playbackState == Player.STATE_ENDED) {
                // When audio is finished playing. restart and pause
                player.seekTo(0);
                player.setPlayWhenReady(false);
                stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        player.getCurrentPosition(),
                        1f);
            }

            mediaSessionCompat.setPlaybackState(stateBuilder.build());
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Timber.d(error);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("SERVICE ON DESTROY CALLED");
        // updates the sharedPrefs to show that the service was destroyed
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(getString(R.string.service_was_started_key), false);
        mEditor.apply();
        playerNotificationManager.setPlayer(null);
        releasePlayer();
        releaseMediaSession();
        stopSelf();

    }

    private void savePlayerPosition() {
        Timber.d("SERVICE SAVE PLAYER POSITION CALLED");
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(this.getString(R.string.last_played_position_key), player.getCurrentPosition());
        mEditor.apply();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("ONSTART COMMAND RUNNING");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        // If the intent that started the activity has a player position other than zero and contains the boolean to confirm,
        // we are starting from a widget click and are continuing playback from the most recent playback event
        long playerPosition = intent.getLongExtra(this.getString(R.string.last_played_position_key), 0);
        Timber.d("PLAYER POSITION: %s", playerPosition);
        boolean startedFromWidget = intent.getBooleanExtra(this.getString(R.string.started_from_widget_key), false);
        Timber.d("StartedFromWidget: %s", startedFromWidget);
        boolean serviceStarted = sharedPreferences.getBoolean(getString(R.string.service_was_started_key), false);
        Timber.d("serviceStarted: %s", serviceStarted);

        if (playerPosition != 0 && startedFromWidget && !serviceStarted){
            player.seekTo(playerPosition);
        }

        return START_STICKY;
    }

    private void releaseMediaSession() {
        mediaSessionCompat.setActive(false);
        mediaSessionCompat.release();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Timber.d("SERVICE ON TASK REMOVED CALLED");
        // updates the sharedPrefs to show that the service was destroyed and not actively running
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(getString(R.string.service_was_started_key), false);
        mEditor.apply();
        releasePlayer();
        releaseMediaSession();
        playerNotificationManager.setPlayer(null);
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    // Releases exoPlayer resources
    private void releasePlayer() {
        Timber.d("SERVICE RELEASE PLAYER CALLED");
        if (player != null) {
            Timber.d("SERVICE PLAYER NOT NULL. RELEASING NOW");
            savePlayerPosition();
            player.stop();
            player.release();
            ExoPlayerUtils.setPlayerToNull();
            player = null;
        } else {
            Timber.d(" SERVICE PLAYER IS ALREADY NULL");
        }

    }

    public class MyFocusListener implements AudioManager.OnAudioFocusChangeListener {

        private boolean requestAudioFocus() {
            Timber.d("REQUESTING AUDIO FOCUS NOW");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return (mAudioManager.requestAudioFocus(mFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
            } else {
                final int result = mAudioManager.requestAudioFocus(this,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
                return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            }
        }

        private void abandonAudioFocus() {
            mAudioManager.abandonAudioFocus(this);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // Play Audio
                    Timber.d("AudioFocus GAIN");
                    player.setPlayWhenReady(true);
                    mediaSessionCompat.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                            player.getCurrentPosition(), 1f).build());
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // PAUSE AUDIO instead of ducking
                    Timber.d("AudioFocus LOSS_TRANSIENT_CAN_DUCK");
                    player.setPlayWhenReady(false);
                    mediaSessionCompat.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                            player.getCurrentPosition(), 1f).build());
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // PAUSE AUDIO
                    Timber.d("AudioFocus LOSS_TRANSIENT");
                    player.setPlayWhenReady(false);
                    mediaSessionCompat.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                            player.getCurrentPosition(), 1f).build());
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Timber.d("AudioFocus LOSS");
                    mediaSessionCompat.setActive(false);
                    player.setPlayWhenReady(false);
                    releasePlayer();
                    mAudioManager.abandonAudioFocus(this);
                    break;
            }
        }
    }

    // static method that gets existing player from the service into our player activity
    // and avoids re-creating a player
    public static SimpleExoPlayer getExistingPlayer() {
        return player;
    }
}
