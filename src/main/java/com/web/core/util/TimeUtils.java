package com.web.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {


    /**
     * Todays date in yyyyMMdd format
     */
    public static String getTodaysDate() {
        return (new SimpleDateFormat ("yyyyMMdd").format (new Date ()));
    }

    /**
     * Current time in HHmmssSSS
     */
    public static String getSystemTime() {
        return (new SimpleDateFormat ("HHmmssSSS").format (new Date ()));
    }
}
