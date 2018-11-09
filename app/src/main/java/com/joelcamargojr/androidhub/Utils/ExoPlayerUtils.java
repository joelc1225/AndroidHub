package com.joelcamargojr.androidhub.Utils;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayerFactory;
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
import com.joelcamargojr.androidhub.R;

import timber.log.Timber;

public class ExoPlayerUtils {

    private static SimpleExoPlayer player;

    // if the player is null, creates a new player. Otherwise returns existing
    public static SimpleExoPlayer createExoPlayer(Context context) {

        if (player == null) {
            Timber.d("PLAYER IS NULL. CREATING NEW ONE");
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

            return player;
        } else {
            Timber.d("PLAYER ALREadY EXISTS. RETURNING SAME PLAYER");
            return player;
        }
    }

    public static void preparePlayer(Context context, SimpleExoPlayer player, String mediaUrlString) {

        Timber.d("AUDIO URL IS: %s", mediaUrlString);
        // converts audio URL to URI
        Uri mediaUri = Uri.parse(mediaUrlString);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getResources().getString(R.string.app_name)), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaUri);
        // Prepare the player with the source.
        player.prepare(audioSource);
    }

    // method to make player null from service
    public static void setPlayerToNull() {
        player = null;
    }
}
