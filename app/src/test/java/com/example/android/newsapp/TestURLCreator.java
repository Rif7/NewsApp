package com.example.android.newsapp;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestURLCreator {
    private URLCreator mCreator;
    private final String KEY = "edbd5c14-5eed-4f30-ba18-8b621faf2b5b";

    @Before
    public void createInstance() {
        mCreator = new URLCreator();
    }

    @Test
    public void testBaseUrl(){
        assertEquals("https://content.guardianapis.com/search?&api-key=" + KEY,
                mCreator.createLink());
    }

    @Test
    public void testSingleSearch(){
        String toSearch = "debate";
        mCreator.addSearchQuery(toSearch);
        assertEquals("https://content.guardianapis.com/search?q=" + toSearch + "&api-key=" + KEY,
                mCreator.createLink());
    }

    @Test
    public void testSearchWithEmptySpace(){
        String searchParameter = "debate economy";
        String searchResult = "\"debate%20economy\"";

        mCreator.addSearchQuery(searchParameter);
        assertEquals("https://content.guardianapis.com/search?q=" + searchResult +  "&api-key=" + KEY,
                mCreator.createLink());
    }


    @Test
    public void testReferences(){
        String toSearch = "contributor";
        mCreator.addTagQuery(toSearch);
        assertEquals("https://content.guardianapis.com/search?&api-key=" + KEY
                        + "&show-tags=" + toSearch,
                mCreator.createLink());
    }

    @Test
    public void testOrderBy(){
        mCreator.orderByNewest();
        assertEquals("https://content.guardianapis.com/search?&api-key=" + KEY
                        + "&order-by=newest",
                mCreator.createLink());
    }

    @Test
    public void testShowFields(){
        String toSearch = "shortUrl";
        mCreator.addShowFieldsQuery(toSearch);
        assertEquals("https://content.guardianapis.com/search?&api-key=" + KEY
                        + "&show-fields=" + toSearch,
                mCreator.createLink());
    }



}