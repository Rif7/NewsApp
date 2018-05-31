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
        assertEquals(QueryUtils.DEFAULT_SIZE, list.size()); // default response size
        int i = 0;
        for (Story story: list) {
            i++;
            Log.d("\nStory " + i + ": ", story.toString());
        }
    }

    @Test
    public void testInvalidResponse() {
        List<Story> list = QueryUtils.prepareNews("4847500234"); // impossible to find query
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}