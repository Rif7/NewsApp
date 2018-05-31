package com.example.android.newsapp;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TestDownloader {
    private String response;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void preconditions() {
        response = "NOT CHANGED";
        Log.d("START TEST", "START TEST");
    }

    @After
    public void logResponse() {
        Log.d("END TEST Response:", response);
    }

    @Test
    public void testNotNullResponse() throws ConnectionException {
        URLCreator urlCreator = new URLCreator("");
        Downloader downloader = new Downloader(urlCreator.createLink());
        response = downloader.crateRowData();
        assertNotNull(response);
    }

    @Test
    public void testNoApiKeyResponse() throws ConnectionException {
        thrown.expect(ConnectionException.class);
        thrown.expectMessage(ConnectionException.HTML_CODE); // Expected 401
        Downloader downloader = new Downloader("https://content.guardianapis.com/search?");
        response = downloader.crateRowData();
    }

    @Test
    public void testWrongResponse() throws ConnectionException {
        thrown.expect(ConnectionException.class);
        thrown.expectMessage(ConnectionException.URL_CONNECTION);
        Downloader downloader = new Downloader("https://content.guaaaaardianddapis.com");
        response = downloader.crateRowData();
    }

    @Test
    public void testResponse() throws ConnectionException, NoSuchFieldException, IllegalAccessException {
        Field field = URLCreator.class.getDeclaredField("API_KEY");
        field.setAccessible(true);
        String key = (String )field.get(null);

        String expectedResponse= "{\"response\":{\"status\":\"ok\",\"userTier\":\"developer\",\"total\":1,\"content\":{\"id\":\"business/2014/feb/18/uk-inflation-falls-below-bank-england-target\",\"type\":\"article\",\"sectionId\":\"business\",\"sectionName\":\"Business\",\"webPublicationDate\":\"2014-02-18T11:02:45Z\",\"webTitle\":\"UK inflation falls below Bank of England's 2% target\",\"webUrl\":\"https://www.theguardian.com/business/2014/feb/18/uk-inflation-falls-below-bank-england-target\",\"apiUrl\":\"https://content.guardianapis.com/business/2014/feb/18/uk-inflation-falls-below-bank-england-target\",\"isHosted\":false,\"pillarId\":\"pillar/news\",\"pillarName\":\"News\"}}}";
        Downloader downloader = new Downloader("https://content.guardianapis.com/business/2014/feb/18/uk-inflation-falls-below-bank-england-target?api-key=" + key);
        response = downloader.crateRowData();
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testBitmapDownload() throws ConnectionException{
        Downloader downloader = new Downloader("https://media.guim.co.uk/bb1acca7331a74e8dbf9c062d1f69e4b9535cdc2/0_460_4413_2647/500.jpg");
        Bitmap bitmap = downloader.downloadImage();
        response = bitmap.toString();
        assertEquals(500, bitmap.getWidth());
        assertEquals(300, bitmap.getHeight());
    }

}
