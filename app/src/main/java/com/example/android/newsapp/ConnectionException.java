package com.example.android.newsapp;

import android.content.Context;

class ConnectionException extends Exception {
    // messages for tests and debugging
    public static String URL;
    public static String HTML_CODE;
    public static String URL_CONNECTION;
    public static String PREPARE_STREAM;
    public static String READ_STREAM;
    public static String CLOSE_STREAM;
    
    public static void initializeMessages(Context context) {
        URL = context.getString(R.string.connection_exception_URL);
        HTML_CODE = context.getString(R.string.connection_exception_HTML_CODE);
        URL_CONNECTION = context.getString(R.string.connection_exception_URL_CONNECTION);
        PREPARE_STREAM = context.getString(R.string.connection_exception_PREPARE_STREAM);
        READ_STREAM = context.getString(R.string.connection_exception_READ_STREAM);
        CLOSE_STREAM = context.getString(R.string.connection_exception_CLOSE_STREAM);
    }

    ConnectionException(String message) {
        super(message);
    }
}
