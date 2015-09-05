package com.lorem_ipsum.utils;

import android.util.Log;

import com.lorem_ipsum.configs.AppConfigs;

/**
 * Created by originally.us on 4/13/14.
 */
public final class LogUtils {

    public static void logInDebug(String logTag, String message) {
        if (AppConfigs.isDebug()) {
            Log.w(logTag, message);
        }
    }

    public static void logErrorDebug(String logTag, String message) {
        if (AppConfigs.isDebug()) {
            Log.e(logTag, message);
        }
    }

}
