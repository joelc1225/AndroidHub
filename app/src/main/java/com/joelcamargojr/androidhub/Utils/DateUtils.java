package com.joelcamargojr.androidhub.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    // Converts the date long object to a human readable string
    public static String convertLongDateToString(Long longDate) {
        Date date = new Date(longDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        return simpleDateFormat.format(date);

    }
}
