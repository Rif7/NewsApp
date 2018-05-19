package com.example.android.newsapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public final class QueryUtils {

    public static List<Story> prepareNews() {
        // Create URL to get data
        // Get the row data
        // Parse data
        // Return List of Stories
        return null;
    }
}

/**
 * Helper object to parametrize query to generate proper URL.
 */
class URLCreator {
    private static final String API_KEY = "edbd5c14-5eed-4f30-ba18-8b621faf2b5b";
    private static final String GUARDIAN_DOMAIN = "https://content.guardianapis.com/search?";

    private String searchQuery = "";
    private String references = "";

    private String getApiKeyParameter() {
        return "&api-key=" + API_KEY;
    }

    public String createLink() {
        return GUARDIAN_DOMAIN + searchQuery + getApiKeyParameter() + references;
    }

    public void addSearchQuery(String searchQuery) {
        String formattedQuery = searchQuery.replaceAll(" ", "%20");
        this.searchQuery = "q=" + formattedQuery;
    }

    public void addReferencesQuery(String references) {
        this.references = "&show-references=" + references;
    }
}

/**
 * Object responsible to download the data from given URL.
 */
class Downloader {
    private static String LOG_TAG = "Downloader";
    private URL url;

    Downloader(String link) throws ConnectionException {
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new ConnectionException(ConnectionException.URL);
        }
    }

    public String crateRowData() throws ConnectionException{
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String response;
        try {
            urlConnection = establishUrlConnection();
            inputStream = prepareInputStream(urlConnection);
            response = readFromStream(inputStream);
        } finally {
            release(urlConnection, inputStream);
        }
        return response;
    }

    private HttpURLConnection establishUrlConnection() throws ConnectionException {
        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() != 200) {
                throw new ConnectionException(ConnectionException.HTML_CODE +
                        urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectionException(ConnectionException.URL_CONNECTION);
        }
        return urlConnection;
    }

    private InputStream prepareInputStream(HttpURLConnection urlConnection)
            throws ConnectionException {
        try {
            return urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectionException(ConnectionException.PREPARE_STREAM);
        }
    }

    private String readFromStream(InputStream inputStream) throws ConnectionException {
        StringBuilder output = new StringBuilder();
        try {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                        Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectionException(ConnectionException.READ_STREAM);
        }
        return output.toString();
    }

    private void release(HttpURLConnection urlConnection, InputStream inputStream)
            throws ConnectionException {
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ConnectionException(ConnectionException.CLOSE_STREAM);
            }

        }
    }
}

/**
 * Object responsible to prepare organize data from json and create objects.
 */
class Parser {
    Parser(String rowData) {

    }

    public List<Story> createList() {
        return null;
    }

}
