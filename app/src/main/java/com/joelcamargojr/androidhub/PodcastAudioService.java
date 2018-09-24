package com.joelcamargojr.androidhub;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.joelcamargojr.androidhub.Utils.ExoPlayerUtils;
import com.joelcamargojr.androidhub.Utils.NotificationUtils;
import com.joelcamargojr.androidhub.model.Episode;

import java.util.List;

import timber.log.Timber;

public class PodcastAudioService extends MediaBrowserServiceCompat {

    private final String TAG = PodcastAudioService.class.getSimpleName();
    private final String MEDIA_ROOT_ID = "media_root_id";
    private final String EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private static MediaSessionCompat mediaSessionCompat;
    private static MediaMetadataCompat mediaMetadataCompat;
    private PlaybackStateCompat.Builder stateBuilder;
    private SimpleExoPlayer simpleExoPlayer;
    private AudioManager mAudioManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationUtils mNotificationUtils;
    boolean mPlaybackDelayed = false;
    boolean mPlaybackNowAuthorized = false;
    private MyFocusListener mMyFocusListener;
    private AudioAttributes mPlaybackAttributes;
    private AudioFocusRequest mFocusRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        // Gets system audio service and creates audio focus request for future reference
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mMyFocusListener = new MyFocusListener();
        mPlaybackAttributes = new AudioAttributes.Builder()
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

        // Creates the exoplayer
        simpleExoPlayer = ExoPlayerUtils.createExoPlayer(this);
        simpleExoPlayer.addListener(new MyExoPlayerEventListener());

        // Instantiates NotificationUtils instance
        mNotificationUtils = new NotificationUtils(this);

        // Creates MediaButtonReceiver instance
        ComponentName mediaButtonReceiver = new ComponentName(this, MyMediaReceiver.class);

        // Creates media session for podcast player
        mediaSessionCompat = new MediaSessionCompat(this, TAG, mediaButtonReceiver, null);

        // Enables callbacks from media buttons/transport controls
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setMediaButtonReceiver(null);

        // Sets initial state
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        mediaSessionCompat.setPlaybackState(stateBuilder.build());

        mediaSessionCompat.setCallback(new MySessionCallbacks());
        mediaSessionCompat.setActive(true);

        setSessionToken(mediaSessionCompat.getSessionToken());

    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (clientPackageName.equals(getPackageName())) {
            return new BrowserRoot(MEDIA_ROOT_ID, null);
        } else {
            return new BrowserRoot(EMPTY_MEDIA_ROOT_ID, null);
        }

    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        if (TextUtils.equals(EMPTY_MEDIA_ROOT_ID, parentId)) {
            result.sendResult(null);
        }
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
                simpleExoPlayer.setPlayWhenReady(true);
                registerReceiver(becomingNoisyReceiver, intentFilter);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            simpleExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
            // Fast forward 10 seconds
            simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
        }

        @Override
        public void onRewind() {
            super.onRewind();
            // Rewind 10 seconds if more than 10 seconds have elapsed
            if (simpleExoPlayer.getCurrentPosition() >= 10000) {
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
            } else {
                simpleExoPlayer.seekTo(0);
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            // Fast forward 10 seconds
            simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            // Rewind 10 seconds if more than 10 seconds have elapsed
            if (simpleExoPlayer.getCurrentPosition() >= 10000) {
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
            } else {
                simpleExoPlayer.seekTo(0);
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            mMyFocusListener.abandonAudioFocus();
            unregisterReceiver(becomingNoisyReceiver);
        }
    }

    // Broadcast receiver for receiving MEDIA_BUTTON intent from clients and
    // handles unplugging of headphones and/or speakers
    public static class MyMediaReceiver extends BroadcastReceiver {

        public MyMediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("Triggered MediaReceiver: handleIntent");
            MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);
        }
    }

    // Broadcast Receiver that deals with unplugged headphones or BT device disconnect
    public class BecomingNoisyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // if audio headset or speaker is disconnected, will pause audio
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                Timber.d("TRIGGERED ACTION BECOMING NOISY");
                stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        simpleExoPlayer.getCurrentPosition(),
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
                Timber.d("WE ARE PLAYING");
                stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        simpleExoPlayer.getCurrentPosition(),
                        1f);

                // Builds new media notification if one wasn't created yet
                if (mNotificationBuilder == null) {
                    mNotificationBuilder =
                            NotificationUtils.buildPlayerNotification(getApplicationContext(), mediaSessionCompat);
                    mNotificationUtils.getNotificationManager().notify(1100, mNotificationBuilder.build());
                } else {
                    mNotificationUtils.getNotificationManager().notify(1100, mNotificationBuilder.build());
                }
            } else if (playbackState == Player.STATE_READY) {
                Timber.d("WE ARE PAUSED");
                stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        simpleExoPlayer.getCurrentPosition(),
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
        releasePlayer();
        mediaSessionCompat.setActive(false);
        mediaSessionCompat.release();
        mNotificationUtils.getNotificationManager().cancelAll();
        stopSelf();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        releasePlayer();
        mediaSessionCompat.setActive(false);
        mNotificationUtils.getNotificationManager().cancelAll();
        stopSelf();
    }

    // Releases exoPlayer resources
    private void releasePlayer() {
        simpleExoPlayer.stop();
        simpleExoPlayer.release();
        simpleExoPlayer = null;
    }

    private class MyFocusListener implements AudioManager.OnAudioFocusChangeListener {

        private boolean requestAudioFocus() {
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
                    // PLAY AUDIO
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // PAUSE AUDIO
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // PAUSE AUDIO
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mAudioManager.abandonAudioFocus(this);
                    break;
            }
        }

        public MediaMetadataCompat setMetaDataForMediaSession(Context context, Episode podcastEpisode) {
            if (mediaMetadataCompat == null) {
                mediaMetadataCompat = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, podcastEpisode.title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, context.getString(R.string.label_fragmented_podcast))
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (long) podcastEpisode.audio_length).build();

                mediaSessionCompat.setMetadata(mediaMetadataCompat);
            }
            return mediaMetadataCompat;
        }

    }
}
