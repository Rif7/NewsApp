package com.example.android.newsapp;

import java.time.Instant;

public class Story {
    private String webUrl;
    private String webTitle;
    private String sectionName;    private static final String API_KEY = "edbd5c14-5eed-4f30-ba18-8b621faf2b5b";


    // can be null
    private String author;

    // can be null
    // webPublicationDate is received in ISO_INSTANT format "2018-02-21T16:50:39Z"
    private Instant webPublicationDate;

    public Story(String webUrl, String webTitle, String sectionName, String author, String webPublicationDate) {
        this.webUrl = webUrl;
        this.webTitle = webTitle;
        this.sectionName = sectionName;
        this.author = author;
        if (webPublicationDate != null) {
            this.webPublicationDate = setWebPublicationDate(webPublicationDate);
        }

    }

    private Instant setWebPublicationDate(String webPublicationDateAsINSTANT) {
        return null;
    }


}
