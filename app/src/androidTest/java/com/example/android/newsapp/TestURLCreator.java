package com.example.android.newsapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TestURLCreator {
    private URLCreator mCreator;
    private String mLink;
    private Context appContext;

    @Before
    public void createInstanceAndGetContext() {
        appContext = InstrumentationRegistry.getTargetContext();
        mCreator = new URLCreator("", appContext);
    }

    private void verifyDomainAndKey() {
        assertTrue(mLink.startsWith("https://content.guardianapis.com/search?"));
        assertTrue(mLink.matches("(.*[^?&])[?&]+api-key=edbd5c14-5eed-4f30-ba18-8b621faf2b5b(.*)"));
    }

    @After
    public void tearDown() {
        Log.d("END", mLink);
        mCreator = null;
        mLink = null;
    }

    public void testBaseUrl(){
        createLink();
        verifyDomainAndKey();
    }

    private void createLink() {
        mLink = mCreator.createLink();
    }

    @Test
    public void testSingleSearch(){
        String toSearch = "debate";
        mCreator = new URLCreator(toSearch, appContext);
        testBaseUrl();
        assertTrue( mLink.contains("q=" + toSearch));
    }

    @Test
    public void testSearchWithEmptySpace(){
        String searchParameter = "debate economy";
        String searchResult = "debate%20economy";

        mCreator = new URLCreator(searchParameter, appContext);
        testBaseUrl();
        assertTrue(mLink.contains("q=" + searchResult));
    }

    @Test
    public void testAll() {
        String toSearch = "debate";
        mCreator = new URLCreator(toSearch, appContext);
        String tag = "contributor";
        mCreator.addTagQuery(tag);
        mCreator.orderBy("newest");
        String field1 = "thumbnail";
        String field2 = "bodyText";
        mCreator.addShowFieldsQuery(new ArrayList<>(Arrays.asList(field1,field2)));
        int size = 7;
        mCreator.addSizeQuery(size);

        testBaseUrl();
        assertTrue(mLink.contains("q=" + toSearch));
        assertTrue(mLink.contains("&show-tags=" + tag));
        assertTrue(mLink.contains("&order-by=newest"));
        assertTrue(mLink.contains("&show-fields=" + field1 + "%2C" + field2));
        assertTrue(mLink.contains("&page-size=" + Integer.toString(size)));
    }
}