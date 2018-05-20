package com.example.android.newsapp;

class ConnectionException extends Exception {
    public static final String URL = "Problem building the URL ";
    public static final String HTML_CODE = "Wrong Html response code: ";
    public static final String URL_CONNECTION = "Problem with establishing Url Connection.";
    public static final String PREPARE_STREAM = "Problem with preparing the stream.";
    public static final String READ_STREAM = "Problem with reading from stream.";
    public static final String CLOSE_STREAM = "Problem with closing the stream.";

    ConnectionException(String message) {
        super(message);
    }
}
