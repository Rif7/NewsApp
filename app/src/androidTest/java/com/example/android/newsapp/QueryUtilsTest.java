package com.example.android.newsapp;

import android.util.Log;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class QueryUtilsTest {

    @Test
    public void testValidResponse() {
        List<Story> list = QueryUtils.prepareNews(""); // default query
        assertNotNull(list);
        assertEquals(10, list.size()); // default response size
        int i = 0;
        for (Story story: list) {
            i++;
            Log.d("\nStory " + i + ": ", story.toString());
        }
    }

    @Test
    public void prepareNews() {
        List<Story> list = QueryUtils.prepareNews("yooouu cannottt fiind meee"); // impossible to find query
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}