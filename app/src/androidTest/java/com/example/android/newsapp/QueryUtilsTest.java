package com.example.android.newsapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class QueryUtilsTest {
    private Context appContext;

    @Before
    public void setContext() {
        appContext = InstrumentationRegistry.getTargetContext();

    }

    @Test
    public void testValidResponse() {
        List<Story> list = QueryUtils.prepareNews("", appContext); // default query
        assertNotNull(list);
        int i = 0;
        for (Story story: list) {
            i++;
            Log.d("\nStory " + i + ": ", story.toString());
        }
    }

    @Test
    public void testInvalidResponse() {
        List<Story> list = QueryUtils.prepareNews("4847500234", appContext); // impossible to find query
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}