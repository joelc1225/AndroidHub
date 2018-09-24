package com.joelcamargojr.androidhub.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.activities.EpisodePlayerActivity;

import timber.log.Timber;

// Helper class to manage notification channels
public class NotificationUtils extends ContextWrapper {

    private NotificationManager notificationManager;
    public static final String PLAYER_CHANNEL = "channel_id_player";
    public static Context mContext;

    /**
     * Registers Notification channels, which can be used later by individual notifications
     *
     * @param context The application context
     */
    public NotificationUtils(Context context) {
        super(context);

        mContext = context;
        // Creates mandatory channel for oreo devices and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(PLAYER_CHANNEL,
                    "Podcast Player Control", NotificationManager.IMPORTANCE_LOW);

            channel1.setLightColor(getColor(R.color.colorPrimaryLight));
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getNotificationManager().createNotificationChannel(channel1);
        }
    }

    // Build podcast player notification
    public static NotificationCompat.Builder buildPlayerNotification(Context context, MediaSessionCompat mediaSession) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PLAYER_CHANNEL);

        int icon;
        String play_pause;
        MediaControllerCompat controllerCompat = mediaSession.getController();

        if (controllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_notification_pause;
            play_pause = context.getString(R.string.pause_label);
        } else {
            icon = R.drawable.exo_notification_play;
            play_pause = context.getString(R.string.play_label);
        }

        // Creates actions for player notification
        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action rewindTenAction = new NotificationCompat.Action(
                R.drawable.noti_rewind_10,
                context.getString(R.string.label_rewind),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_REWIND));

        Intent startPlayerIntent = new Intent(context, EpisodePlayerActivity.class);
        startPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Timber.d("ACTION SET TO:" + String.valueOf(SystemClock.uptimeMillis()));
        startPlayerIntent.setAction(String.valueOf(SystemClock.uptimeMillis()));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, 0, startPlayerIntent, 0);

        builder.setContentTitle(context.getString(R.string.label_fragmented_podcast))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(getSmallIcon())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .addAction(playPauseAction)
                .addAction(rewindTenAction)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));

        return builder;
    }

    // Gets small icon
    private static int getSmallIcon() {
        return R.drawable.noti_play;
    }

    // Gets the notification manager
    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

}
