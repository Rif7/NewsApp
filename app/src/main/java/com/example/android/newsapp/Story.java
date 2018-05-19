package com.example.android.newsapp;

import android.app.admin.DeviceAdminInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Story {
    private static final String API_KEY = "edbd5c14-5eed-4f30-ba18-8b621faf2b5b";

    private String webUrl;
    private String webTitle;
    private String sectionName;

    // can be null
    private ArrayList<String> authors;

    // can be null
    // webPublicationDate is received in ISO_INSTANT format "2018-02-21T16:50:39Z"
    private Date webPublicationDate;

    public Story(String webUrl, String webTitle, String sectionName, ArrayList<String> authors, String webPublicationDate) {
        this.webUrl = webUrl;
        this.webTitle = webTitle;
        this.sectionName = sectionName;
        this.authors = authors;
        if (webPublicationDate != null) {
            this.webPublicationDate = setWebPublicationDate(webPublicationDate);
        } else {
            this.webPublicationDate = null;
        }

    }

    private Date setWebPublicationDate(String webPublicationDateAsINSTANT) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK);
        try {
            return formatter.parse(webPublicationDateAsINSTANT.replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getSectionName() {
        return sectionName;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public Date getWebPublicationDate() {
        return webPublicationDate;
    }

}
