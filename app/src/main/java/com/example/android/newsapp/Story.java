package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

class Story {
    private Context context;

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

    Story(Context context, String webUrl, String webTitle, String sectionName,
          ArrayList<String> authors, String webPublicationDate, String imageUrl, String bodyText) {
        this.context = context;
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
        final String UNKNOWN = context.getString(R.string.story_unknown);
        final String SEP = context.getString(R.string.story_separator);
        final String IMAGE_SEPARATOR = context.getString(R.string.story_image_res_separator);
        final String NL = System.getProperty("line.separator");


        StringBuilder s = new StringBuilder();
        s.append(context.getString(R.string.story_webUrl)).append(SEP).append(webUrl).append(NL);
        s.append(context.getString(R.string.story_webTitle)).append(SEP).append(webTitle).append(NL);
        s.append(context.getString(R.string.story_sectionName)).append(SEP).append(sectionName).append(NL);

        s.append(context.getString(R.string.story_authors)).append(SEP);
        if (authors != null) {
            s.append(Arrays.toString(authors.toArray()));
        } else {
            s.append(UNKNOWN);
        }
        s.append(NL);

        s.append(context.getString(R.string.story_webPublicationDate)).append(SEP);
        if (webPublicationDate != null) {
            s.append(webPublicationDate.toString());
        } else {
            s.append(UNKNOWN);
        }
        s.append(NL);

        s.append(context.getString(R.string.story_imageUrl)).append(SEP);
        if (imageUrl != null) {
            s.append(imageUrl);
        } else {
            s.append(UNKNOWN);
        }
        s.append(NL);

        s.append(context.getString(R.string.story_image)).append(SEP);
        if (image != null) {
            s.append(image.getWidth()).append(IMAGE_SEPARATOR).append(image.getHeight());
        } else {
            s.append(UNKNOWN);
        }
        s.append(NL);

        s.append(context.getString(R.string.story_bodyText)).append(SEP);
        if (bodyText != null) {
            s.append(bodyText);
        } else {
            s.append(UNKNOWN);
        }
        s.append(NL);

        return s.toString();
    }
}
