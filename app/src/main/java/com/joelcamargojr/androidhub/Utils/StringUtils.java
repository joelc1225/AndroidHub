package com.joelcamargojr.androidhub.Utils;

import android.text.Html;

public class StringUtils {

    // Strips HTML tags from Strings received from API
    public static String stripHtml(String htmlString) {

        // returns with all HTML tags
//        return htmlString;

        // returns cleaned up and presentable string
        return Html.fromHtml(htmlString).toString();

    }
}
