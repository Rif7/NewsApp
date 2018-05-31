package com.example.android.newsapp;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

class Story {
    private static final String UNKNOWN = "UNKNOWN";

    private String webUrl;
    private String webTitle;
    private String sectionName;

    // can be null
    private ArrayList<String> authors;
    private Date webPublicationDate;    // webPublicationDate is received as ISO_INSTANT format
                                        // "2018-02-21T16:50:39Z"
    private String imageUrl;
    Bitmap image;
    private String bodyText;

    public Story(String webUrl, String webTitle, String sectionName, ArrayList<String> authors,
                 String webPublicationDate, String imageUrl, String bodyText) {
        this.webUrl = webUrl;
        // delete everything  after '|' from title
        if (webTitle.contains("|")) {
            webTitle = webTitle.substring(0, webTitle.indexOf('|'));
        }
        this.webTitle = webTitle;
        this.sectionName = sectionName;
        this.authors = authors;
        if (webPublicationDate != null) {
            this.webPublicationDate = setWebPublicationDate(webPublicationDate);
        } else {
            this.webPublicationDate = null;
        }
        this.imageUrl = imageUrl;
        this.bodyText = bodyText;
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

    public String getImageUrl() { return imageUrl; }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getBodyText() {
        return bodyText;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("webUrl = ").append(webUrl).append("\n");
        s.append("webTitle = ").append(webTitle).append("\n");
        s.append("sectionName = ").append(sectionName).append("\n");

        s.append("authors = ");
        if (authors != null) {
            s.append(Arrays.toString(authors.toArray()));
        } else {
            s.append(UNKNOWN);
        }
        s.append("\n");

        s.append("webPublicationDate = ");
        if (webPublicationDate != null) {
            s.append(webPublicationDate.toString());
        } else {
            s.append(UNKNOWN);
        }
        s.append("\n");

        s.append("imageUrl = ");
        if (imageUrl != null) {
            s.append(imageUrl);
        } else {
            s.append(UNKNOWN);
        }
        s.append("\n");

        s.append("image = ");
        if (image != null) {
            s.append(image.getWidth()).append("x").append(image.getHeight());
        } else {
            s.append(UNKNOWN);
        }
        s.append("\n");

        s.append("bodyText = ");
        if (bodyText != null) {
            s.append(bodyText);
        } else {
            s.append(UNKNOWN);
        }
        s.append("\n");

        return s.toString();
    }
}
