package com.example.android.newsapp;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TestParser {
    @Before
    public void preconditions() {
        Log.d("START TEST", "START TEST");
    }


    private void assertEqualsNews(ArrayList<Story> expected, ArrayList<Story> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i=0; i<expected.size(); i++) {
            assertEquals(Arrays.toString(expected.get(i).getAuthors().toArray()), Arrays.toString(actual.get(i).getAuthors().toArray()));
            assertEquals(expected.get(i).getSectionName(), actual.get(i).getSectionName());
            assertEquals(expected.get(i).getWebUrl(), actual.get(i).getWebUrl());
            assertEquals(expected.get(i).getWebTitle(), actual.get(i).getWebTitle());
            assertEquals(expected.get(i).getWebPublicationDate().toString(), actual.get(i).getWebPublicationDate().toString());
            assertEquals(expected.get(i).getImageUrl(), actual.get(i).getImageUrl());
            assertEquals(expected.get(i).getBodyText(), actual.get(i).getBodyText());
            Log.d("\nActual Story: ", actual.toString());
        }
    }

    @Test
    public void parseTypicalJson() throws JSONException {
        String json = "{\"response\":{\"status\":\"ok\",\"userTier\":\"developer\",\"total\":5,\"startIndex\":1,\"pageSize\":1,\"currentPage\":1,\"pages\":5,\"orderBy\":\"relevance\",\"results\":[{\"id\":\"politics/2018/may/10/tories-accused-of-subverting-democracy-by-not-tabling-brexit-debates\",\"type\":\"article\",\"sectionId\":\"politics\",\"sectionName\":\"Politics\",\"webPublicationDate\":\"2018-05-10T16:09:05Z\",\"webTitle\":\"Tories accused of 'subverting democracy' by not tabling Brexit debates\",\"webUrl\":\"https://www.theguardian.com/politics/2018/may/10/tories-accused-of-subverting-democracy-by-not-tabling-brexit-debates\",\"apiUrl\":\"https://content.guardianapis.com/politics/2018/may/10/tories-accused-of-subverting-democracy-by-not-tabling-brexit-debates\",\"fields\":{\"thumbnail\":\"https://media.guim.co.uk/4c7d2b9efcdda9c4f5471267b2bd89db7ea288c4/24_0_3451_2070/500.jpg\",\"bodyText\":\"BODY TEXT\"},\"tags\":[{\"id\":\"profile/peterwalker\",\"type\":\"contributor\",\"webTitle\":\"Peter Walker\",\"webUrl\":\"https://www.theguardian.com/profile/peterwalker\",\"apiUrl\":\"https://content.guardianapis.com/profile/peterwalker\",\"references\":[],\"bio\":\"<p>Peter Walker is a political correspondent for the Guardian and author of Bike Nation: How Cycling Can Save the World</p>\",\"bylineImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2007/09/28/peter_walker_140x140.jpg\",\"firstName\":\"walker\",\"lastName\":\"\",\"twitterHandle\":\"peterwalker99\"},{\"id\":\"profile/jessica-elgot\",\"type\":\"contributor\",\"webTitle\":\"Jessica Elgot\",\"webUrl\":\"https://www.theguardian.com/profile/jessica-elgot\",\"apiUrl\":\"https://content.guardianapis.com/profile/jessica-elgot\",\"references\":[],\"bio\":\"<p>Jessica Elgot is a Guardian political correspondent. Twitter:&nbsp;<a href=\\\"https://twitter.com/jessicaelgot\\\">@jessicaelgot</a></p>\",\"bylineImageUrl\":\"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2015/6/26/1435313697913/Jessica-Elgot.jpg\",\"bylineLargeImageUrl\":\"https://uploads.guim.co.uk/2017/10/06/Jessica-Elgot,-R.png\",\"firstName\":\"Jessica\",\"lastName\":\"Elgot\",\"twitterHandle\":\"jessicaelgot\"}],\"isHosted\":false,\"pillarId\":\"pillar/news\",\"pillarName\":\"News\"}]}}";
        ArrayList<Story> expected = new ArrayList<>();
        expected.add(new Story("https://www.theguardian.com/politics/2018/may/10/tories-accused-of-subverting-democracy-by-not-tabling-brexit-debates",
                "Tories accused of 'subverting democracy' by not tabling Brexit debates",
                "Politics",
                new ArrayList<>(Arrays.asList("Peter Walker", "Jessica Elgot")),
                "2018-05-10T16:09:05Z",
                "https://media.guim.co.uk/4c7d2b9efcdda9c4f5471267b2bd89db7ea288c4/24_0_3451_2070/500.jpg",
                "BODY TEXT"));

        Parser parser = new Parser(json);
        ArrayList<Story> actual = (ArrayList<Story>) parser.createList();
        assertEqualsNews(expected, actual);
    }
}
