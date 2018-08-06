package com.joelcamargojr.androidhub.Utils;

import android.text.Html;

public class StringUtils {

    // Strips HTML tags from Strings received from API
    public static String stripHtml(String htmlString) {
        return Html.fromHtml(htmlString).toString();
    }
}
