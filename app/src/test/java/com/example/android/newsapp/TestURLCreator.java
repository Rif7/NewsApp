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
                mCreator.createURL());
    }

    @Test
    public void testSingleSearch(){
        String toSearch = "debate";
        mCreator.addSearchQuery(toSearch);
        assertEquals("https://content.guardianapis.com/search?q=" + toSearch + "&api-key=" + KEY,
                mCreator.createURL());
    }

    @Test
    public void testSearchWithEmptySpace(){
        String searchParameter = "debate AND economy";
        String searchResullt = "debate%20AND%20economy";

        mCreator.addSearchQuery(searchParameter);
        assertEquals("https://content.guardianapis.com/search?q=" + searchResullt +  "&api-key=" + KEY,
                mCreator.createURL());
    }

    @Test
    public void testReferences(){
        String toSearch = "author";
        mCreator.addReferencesQuery(toSearch);
        assertEquals("https://content.guardianapis.com/search?&api-key=" + KEY
                        + "&show-references=" + toSearch,
                mCreator.createURL());
    }


}