package com.joelcamargojr.androidhub.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaMetadataCompat;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.model.Episode;

public class MetaDataUtils  {

    private static MediaMetadataCompat mediaMetadataCompat;

    public static MediaMetadataCompat setMetaDataForMediaSession(
            Context context, Episode podcastEpisode) {

        // If fresh startup, creates new metadata object
        if (mediaMetadataCompat == null) {
            updateMetaData(context, podcastEpisode);
            return mediaMetadataCompat;
        } else {
            // Check if Episode data is different to see if updating is necessary
            if (mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE).equals(podcastEpisode.title)) {
                return mediaMetadataCompat;
            } else {
                updateMetaData(context, podcastEpisode);
                return mediaMetadataCompat;
            }
        }
    }

    private static void updateMetaData(Context context, Episode podcastEpisode) {
        mediaMetadataCompat = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, podcastEpisode.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, context.getString(R.string.label_fragmented_podcast))
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getBitmap(context))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (long) podcastEpisode.audio_length).build();
    }

    public static MediaMetadataCompat getMediaMetadata() {
        return mediaMetadataCompat;
    }

    private static Bitmap getBitmap(Context context) {
        return  BitmapFactory.decodeResource(context.getResources(), R.drawable.fragmented_image);
    }
}
