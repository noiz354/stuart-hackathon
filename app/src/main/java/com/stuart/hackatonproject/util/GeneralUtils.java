package com.stuart.hackatonproject.util;

import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.util.Date;

/**
 * Created by nathan on 10/18/17.
 */

public class GeneralUtils {

    public static String generateReadableTime(long timestamp) {
        long now = System.currentTimeMillis();
        String dateFormat = DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.DAY_IN_MILLIS).toString();
        dateFormat += ", " + DateFormat.format("h:mm a", new Date(timestamp));
        return dateFormat;
    }
}