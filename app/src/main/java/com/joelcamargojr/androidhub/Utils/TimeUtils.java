package com.joelcamargojr.androidhub.Utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    // Converts the length of the audio file from int to String that we get from API
    public static String secondsToMinutes(int seconds) {
        return String.valueOf(TimeUnit.SECONDS.toMinutes(seconds));
    }
}
