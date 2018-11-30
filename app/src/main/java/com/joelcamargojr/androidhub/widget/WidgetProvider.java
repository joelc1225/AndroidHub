package com.joelcamargojr.androidhub.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.joelcamargojr.androidhub.activities.MainActivity;
import com.joelcamargojr.androidhub.R;
import com.joelcamargojr.androidhub.activities.EpisodePlayerActivity;
import com.joelcamargojr.androidhub.model.Episode;

import org.parceler.Parcels;

import java.util.Objects;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    public static final String ACTION_WIDGET_CLICK = "widgetClick";
    public static final String ACTION_WIDGET_CLICK_WITH_TOAST ="widgetClickWithToast";
    private static final int INTENT_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT;
    private static final int REQUEST_CODE = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Timber.d("UPDATE APP WIDGET");

        // Gets the app's sharedPrefs
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);

        // Gets last played episode title for widget text
        CharSequence widgetText =
                sharedPreferences.getString(context.getString(R.string.last_played_episode_key), context.getString(R.string.widget_empty_string));

        // If text equals the empty string, we'll open up mainActivity to prompt the user to click an episode
        // otherwise, we'll open up the episodePlyaerActivity with the data from the most recently played episode
        if (widgetText == context.getString(R.string.widget_empty_string)) {

            Intent clickIntent = new Intent(context, WidgetProvider.class);
            clickIntent.setAction(ACTION_WIDGET_CLICK_WITH_TOAST);

            PendingIntent widgetPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, clickIntent, INTENT_FLAGS);

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewText(R.id.appwidget_text, widgetText);
            views.setOnClickPendingIntent(R.id.widget_button, widgetPendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } else {
            // Instantiates data to create an episode object inside our activity when it opens
            String episodeTitle = (String) widgetText;
            String episodeAudioUrl = sharedPreferences.getString(context.getString(R.string.last_played_audioUrl_key), null);
            String episodeDescription = sharedPreferences.getString(context.getString(R.string.last_played_episode_description_key), null);
            int episodeLength = sharedPreferences.getInt(context.getString(R.string.last_played_audio_length_key), 0);
            String episodeId = sharedPreferences.getString(context.getString(R.string.last_played_id_key), null);
            long episodeDate = sharedPreferences.getLong(context.getString(R.string.last_played_date_key), 0);
            String listenNotesUrl = sharedPreferences.getString(context.getString(R.string.last_played_listenNotesUrl_key), null);

            // Create the intent/pending intent for the buttons of the widget
            Intent clickIntent = new Intent(context, EpisodePlayerActivity.class);
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelable(context.getString(R.string.episode_key), Parcels.wrap(new Episode(episodeTitle, episodeAudioUrl,
                    episodeLength, episodeId, episodeDescription, episodeDate, listenNotesUrl)));

            clickIntent.putExtra(context.getString(R.string.bundle_key), bundle);


            // Sets actions for the intents
            clickIntent.setAction(ACTION_WIDGET_CLICK);

            PendingIntent widgetPendingIntent = TaskStackBuilder.create(context)
                    .addNextIntent(mainActivityIntent)
                    .addNextIntentWithParentStack(clickIntent)
                    .getPendingIntent(INTENT_FLAGS, REQUEST_CODE);

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewText(R.id.appwidget_text, widgetText);
            views.setOnClickPendingIntent(R.id.widget_button, widgetPendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Timber.d("WIDGET ON RECEIVE");
        Timber.d("ACTION IS: %s", intent.getAction());
        if (Objects.equals(intent.getAction(), ACTION_WIDGET_CLICK_WITH_TOAST)) {
            Toast.makeText(context, R.string.widgetToastMessage, Toast.LENGTH_SHORT).show();

            Intent openMainActivityIntent = new Intent(context, MainActivity.class);
            context.startActivity(openMainActivityIntent);
        }
    }
}

