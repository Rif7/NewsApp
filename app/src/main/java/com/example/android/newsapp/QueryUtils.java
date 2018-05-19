package com.example.android.newsapp;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
    String json;

    Parser(String rowData) {
        json = rowData;
    }

    private ArrayList<Story> parseResults(JSONArray resultsArray) throws JSONException{
        ArrayList<Story> stories = new ArrayList<>();
        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject currentStory = resultsArray.getJSONObject(i);
            stories.add(new Story(
                    currentStory.getString("webUrl"),
                    currentStory.getString("webTitle"),
                    currentStory.getString("sectionName"),
                    getAuthors(currentStory),
                    getWebPublicationDate(currentStory)
            ));
        }
        return stories;
    }

    private String getWebPublicationDate(JSONObject currentStory) throws JSONException {
        String webPublicationDateKey = "webPublicationDate";
        if (currentStory.has(webPublicationDateKey)) {
            return currentStory.getString(webPublicationDateKey);
        } else{
            return null;
        }
    }

    @Nullable
    private ArrayList<String> getAuthors(JSONObject currentStory) throws JSONException {
        String tagsKey = "tags";

        ArrayList<String> authors = new ArrayList<>();

        if (currentStory.has(tagsKey)) {
            JSONArray tagsArray = currentStory.getJSONArray(tagsKey);
            for (int i = 0; i < tagsArray.length(); i++) {
                JSONObject currentTag = tagsArray.getJSONObject(i);
                String author = getAuthor(currentTag);
                if (author != null) {
                    authors.add(author);
                }
            }
        }
        if (authors.isEmpty()) {
            return null;
        } else {
            return authors;
        }

    }

    @Nullable
    private String getAuthor(JSONObject currentTag) throws JSONException {
        String typeKey = "type";
        String typeValue = "contributor";
        String authorKey = "webTitle";
        if (currentTag.has(typeKey) && currentTag.get(typeKey).equals(typeValue) && currentTag.has(authorKey)) {
            return currentTag.getString(authorKey);
        }
        return null;
    }

    public List<Story> createList() throws JSONException {
        JSONObject baseJson = new JSONObject(json);
        JSONObject responseJson = baseJson.getJSONObject("response");
        JSONArray storiesArray = responseJson.getJSONArray("results");
        return parseResults(storiesArray);
    }

}
