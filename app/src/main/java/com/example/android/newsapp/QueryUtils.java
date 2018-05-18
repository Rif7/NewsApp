package com.example.android.newsapp;

import java.net.URL;
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

    public String createURL() {
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

    Downloader(URL url){

    }
    public String crateRowData() {
        return null;
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
