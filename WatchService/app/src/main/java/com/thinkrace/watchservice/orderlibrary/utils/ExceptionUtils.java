package com.thinkrace.watchservice.orderlibrary.utils;

/**
 */
public class ExceptionUtils {
    private static final String TAG = "ExceptionUtils";

    public static void throwException(String message) {
        throw new IllegalStateException(TAG + " : " + message);
    }
}
