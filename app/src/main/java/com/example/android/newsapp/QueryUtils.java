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

    public static List<Story> prepareNews(String searchQuery, Context context) {
        if (ConnectionException.URL_CONNECTION == null) {
            ConnectionException.initializeMessages(context);
        }

        try {
            // Create URL to get data
            URLCreator urlCreator = new URLCreator(searchQuery, context);
            urlCreator.addTagQuery(context.getString(R.string.contributor));

            // get preferences
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

            // add to query fields
            addQueryFields(context, urlCreator, sharedPrefs);

            // add order type
            addOrder(context, urlCreator, sharedPrefs);

            // add number of stories to query
            addPageSizeQuery(context, urlCreator, sharedPrefs);

            // Get the data and parse
            Downloader downloader = new Downloader(urlCreator.createLink());
            Parser parser = new Parser(downloader.crateRowData(), context);
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

    private static void addOrder(Context context, URLCreator urlCreator, SharedPreferences sharedPrefs) {
        String orderByType = sharedPrefs.getString(
                context.getString(R.string.settings_order_by_key),
                context.getString(R.string.settings_order_by_default));
        urlCreator.orderBy(orderByType);
    }

    private static void addPageSizeQuery(Context context, URLCreator urlCreator, SharedPreferences sharedPrefs) {
        int pageSize = Integer.parseInt(sharedPrefs.getString(
                context.getString(R.string.settings_page_size_key),
                context.getString(R.string.settings_page_size_default)));
        urlCreator.addSizeQuery(pageSize);
    }

    private static void addQueryFields(Context context, URLCreator urlCreator, SharedPreferences sharedPrefs) {
        ArrayList<String> list = new ArrayList<>();
        boolean showImages = sharedPrefs.getBoolean(context.getString(R.string.settings_show_images_key), true);
        if (showImages) {
            list.add(context.getString(R.string.thumbnail));
        }

        int bodyTextLength = Integer.parseInt(sharedPrefs.getString(
                context.getString(R.string.settings_show_body_key),
                context.getString(R.string.settings_show_body_default)));
        if (bodyTextLength != Integer.parseInt(context.getString(R.string.settings_show_body_None_value))) {
            list.add(context.getString(R.string.bodyText));
        }
        if (!list.isEmpty()) {
            urlCreator.addShowFieldsQuery(list);
        }
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
    private Uri.Builder query;
    private Context context;

    public String createLink() {
        query.appendQueryParameter(context.getString(R.string.api_param),
                context.getString(R.string.api_key));
        return query.toString();
    }

    URLCreator(String searchQuery, Context context) {
        this.context = context;
        query = Uri.parse(this.context.getString(R.string.guardian_domain)).buildUpon();
        if (searchQuery.equals("")) {
            return;
        }
        query.appendQueryParameter(context.getString(R.string.search_param), searchQuery);
    }

    public void orderBy(String orderType) {
        query.appendQueryParameter(context.getString(R.string.order_by_param), orderType);
    }

    public void addTagQuery(String references) {
        query.appendQueryParameter(context.getString(R.string.show_tags_param), references);
    }

    public void addShowFieldsQuery(List<String> fields ) {
        StringBuilder fieldsToShow = new StringBuilder();
        String prefix = "";
        for (String field:fields) {
            fieldsToShow.append(prefix);
            prefix = context.getString(R.string.separator);
            fieldsToShow.append(field);
        }
        query.appendQueryParameter(context.getString(R.string.show_fields_param), fieldsToShow.toString());
    }

    /**
     * 	Modify the number of items displayed per page	Integer	1 to 50
     */
    public void addSizeQuery(int size){
        query.appendQueryParameter(context.getString(R.string.page_size_param), Integer.toString(size));
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
    private Context context;

    Parser(String rowData, Context context) {
        json = rowData;
        this.context = context;
    }

    private ArrayList<Story> parseResults(JSONArray resultsArray) throws JSONException{
        ArrayList<Story> stories = new ArrayList<>();
        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject currentStory = resultsArray.getJSONObject(i);
            stories.add(new Story(context,
                    currentStory.getString(context.getString(R.string.parse_webUrl)),
                    currentStory.getString(context.getString(R.string.parse_webTitle)),
                    currentStory.getString(context.getString(R.string.parse_sectionName)),
                    getAuthors(currentStory),
                    getWebPublicationDate(currentStory),
                    getField(currentStory,context.getString(R.string.parse_thumbnail)),
                    getField(currentStory,context.getString(R.string.parse_bodyText))
            ));
        }
        return stories;
    }

    private String getWebPublicationDate(JSONObject currentStory) throws JSONException {
        String webPublicationDateKey = context.getString(R.string.parse_webPublicationDate);
        if (currentStory.has(webPublicationDateKey)) {
            return currentStory.getString(webPublicationDateKey);
        } else{
            return null;
        }
    }

    @Nullable
    private ArrayList<String> getAuthors(JSONObject currentStory) throws JSONException {
        String tagsKey = context.getString(R.string.parse_tags);
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
        String typeKey = context.getString(R.string.parse_type);
        String typeValue = context.getString(R.string.parse_contributor);
        String authorKey = context.getString(R.string.parse_contributor_name);
        if (currentTag.has(typeKey) && currentTag.get(typeKey).equals(typeValue) && currentTag.has(authorKey)) {
            return currentTag.getString(authorKey);
        }
        return null;
    }

    @Nullable
    private String getField(JSONObject currentStory, String key) throws JSONException {
        String fieldKey = context.getString(R.string.parse_fields);

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
        JSONObject responseJson = baseJson.getJSONObject(context.getString(R.string.parse_response));
        JSONArray storiesArray = responseJson.getJSONArray(context.getString(R.string.parse_results));
        return parseResults(storiesArray);
    }
}
