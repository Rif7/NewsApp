package com.example.android.newsapp;

import java.net.URL;
import java.util.List;

public final class QuerryUtils {

    public static List<Story> prepareNews() {
        // Create URL to get data
        // Get the row data
        // Parse data
        // Return List of Stories
        return null;
    }

    /**
     * Helper object to parametrize query to generate proper URL.
     */
    public class URLCreator {
        private static final String API_KEY = "edbd5c14-5eed-4f30-ba18-8b621faf2b5b";

        public URL createURL() {
            return null;
        }
    }

    /**
     * Object responsible to download the data from given URL.
     */
    public class Downloader {

        Downloader(URL url){

        }
        public String crateRowData() {
            return null;
        }
    }

    /**
     * Object responsible to prepare organize data from json and create objects.
     */
    public class Parser {
        Parser(String rowData) {

        }

        public List<Story> createList() {
            return null;
        }

    }
}
