//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sharesrc.nss.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    static final String DATE_TIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static final String LOG_FOLDER_NAME_FORMAT = "yyyyMMdd_HHmmss";

    public DateTimeUtil() {
    }

    public static String getTimeNow() {
        return getTimeString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getTimeNow4LogFolderName() {
        return getTimeString(System.currentTimeMillis(), "yyyyMMdd_HHmmss");
    }

    public static String getTimeString(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static String getTimeString(long longDate, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(new Date(longDate));
    }
}
