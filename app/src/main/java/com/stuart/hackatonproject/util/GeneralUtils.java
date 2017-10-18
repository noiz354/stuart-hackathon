package com.stuart.hackatonproject.util;

import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.Date;

/**
 * Created by nathan on 10/18/17.
 */

public class GeneralUtils {

    public static String generateReadableTime(long timestamp) {
        Date date = new Date(timestamp);
        String dateFormat = DateTimeUtils.formatDate(date);
        return dateFormat;
    }
}