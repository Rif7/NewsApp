package com.example.android.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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

final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    public static final int DEFAULT_SIZE = 8;

    public static List<Story> prepareNews(String searchQuery, Context context) {
        try {
            // Create URL to get data
            URLCreator urlCreator = new URLCreator(searchQuery);
            urlCreator.addTagQuery("contributor");

            // get preferences
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

            // add to query fields
            ArrayList<String> list = new ArrayList<>();
            boolean showImages = sharedPrefs.getBoolean(context.getString(R.string.settings_show_images_key), true);
            if (showImages) {
                list.add("thumbnail");
            }
            list.add("bodyText");
            if (!list.isEmpty()) {
                urlCreator.addShowFieldsQuery(list);
            }

            // change default order
            urlCreator.orderByNewest();

            // add number of stories to query
            urlCreator.addSizeQuery(DEFAULT_SIZE);

            // Get the data and parse
            Downloader downloader = new Downloader(urlCreator.createLink());
            Parser parser = new Parser(downloader.crateRowData());
            List<Story> stories = parser.createList();
            if (!stories.isEmpty()) {
                stories = downloadImages(stories);
            }
            return stories;
        } catch (JSONException | ConnectionException e ) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

    private static List<Story> downloadImages(List<Story> stories){
        for (Story story: stories) {
            try {
                String imageUrl = story.getImageUrl();
                if (imageUrl != null) {
                    Downloader downloader = new Downloader(imageUrl);
                    story.setImage(downloader.downloadImage());
                }
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
        }
        return stories;
    }
}

/**
 * Helper object to parametrize query to generate proper URL.
 */
class URLCreator {
    private static final String API_KEY = "edbd5c14-5eed-4f30-ba18-8b621faf2b5b";
    private static final String GUARDIAN_DOMAIN = "https://content.guardianapis.com/search?";

    private Uri.Builder query;

    public String createLink() {
        query.appendQueryParameter("api-key", API_KEY);
        return query.toString();
    }

    URLCreator(String searchQuery) {
        query = Uri.parse(GUARDIAN_DOMAIN).buildUpon();
        if (searchQuery.equals("")) {
            return;
        }
        query.appendQueryParameter("q", searchQuery);
    }

    public void orderByNewest() {
        query.appendQueryParameter("order-by", "newest");
    }

    public void addTagQuery(String references) {
        query.appendQueryParameter("show-tags", references);
    }

    public void addShowFieldsQuery(String fields) {
        query.appendQueryParameter("show-fields", fields);
    }

    public void addShowFieldsQuery(List<String> fields ) {
        StringBuilder fieldsToShow = new StringBuilder();
        String prefix = "";
        for (String field:fields) {
            fieldsToShow.append(prefix);
            prefix = ",";
            fieldsToShow.append(field);
        }
        query.appendQueryParameter("show-fields", fieldsToShow.toString());
    }

    /**
     * 	Modify the number of items displayed per page	Integer	1 to 50
     */
    public void addSizeQuery(int size){
        query.appendQueryParameter("page-size", Integer.toString(size));
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

    /**
     *  This method is used to download articles and theirs text contents
     */
    public String crateRowData() throws ConnectionException{
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String response;
        try {
            urlConnection = establishUrlConnection();
            inputStream = prepareInputStream(urlConnection);
            response = readDataFromStream(inputStream);
        } finally {
            release(urlConnection, inputStream);
        }
        return response;
    }

    /**
     *  This method is used to download image for article
     */
    public Bitmap downloadImage() throws ConnectionException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        Bitmap image;
        try {
            urlConnection = establishUrlConnection();
            inputStream = prepareInputStream(urlConnection);
            image = getBitmapFromStream(inputStream);
        } finally {
            release(urlConnection, inputStream);
        }
        return image;

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

    private String readDataFromStream(InputStream inputStream) throws ConnectionException {
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

    private Bitmap getBitmapFromStream(InputStream inputStream) {
        Bitmap bmp = null;
        if (inputStream != null) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            bmp = BitmapFactory.decodeStream(bufferedInputStream);
        }
        return bmp;
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
    private String json;

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
                    getWebPublicationDate(currentStory),
                    getField(currentStory,"thumbnail"),
                    getField(currentStory,"bodyText")
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

    @Nullable
    private String getField(JSONObject currentStory, String key) throws JSONException {
        String fieldKey = "fields";

        if (currentStory.has(fieldKey)) {
            JSONObject fieldObject = currentStory.getJSONObject(fieldKey);
            if (fieldObject.has(key)) {
                return fieldObject.getString(key);
            }
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
