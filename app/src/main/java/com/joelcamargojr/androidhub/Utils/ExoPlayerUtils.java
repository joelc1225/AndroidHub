package com.joelcamargojr.androidhub.Utils;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import timber.log.Timber;

public class ExoPlayerUtils {

    private static Player.DefaultEventListener listener;
    private static SimpleExoPlayer player;

    public static SimpleExoPlayer createExoPlayer(Context context) {

        if (player == null){
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

            return player;
        } else {
            return player;
        }

    }

    public static void preparePlayer(Context context, String mediaUrl, SimpleExoPlayer player) {

        // converts audio URL to URI
        Uri mediaUri = Uri.parse(mediaUrl);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "AndroidHub"), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaUri);
        // Prepare the player with the source.
        player.prepare(audioSource);
    }

    public static Player.DefaultEventListener getDefaultListener() {

        if (listener == null) {
            listener = new Player.DefaultEventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    super.onPlayerStateChanged(playWhenReady, playbackState);
                    Timber.d("ONPLAYERSTATECHANGED called");

                    if ((playbackState == Player.STATE_READY) && playWhenReady){
                        Timber.d("WE ARE PLAYINGGGGG");
                    } else if (playbackState == Player.STATE_READY) {
                        Timber.d("WE ARE PAUSEDDDD");
                    }
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    super.onPlayerError(error);
                    Timber.d(error);
                }
            };

            return listener;
        } else {
            return listener;
        }
    }

}