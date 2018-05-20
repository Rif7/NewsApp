package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Story>> {
    private static int STORY_LOADER_ID;

    private String searchQuery = "Royal Wedding";
    private StoryAdapter storyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set adapter fot ListView
        ListView storyListView = (ListView) findViewById(R.id.list);
        storyAdapter = new StoryAdapter(this, new ArrayList<Story>());
        storyListView.setAdapter(storyAdapter);

        // Set an item click listener on the ListView

        // Fetch data Initialize loader
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(STORY_LOADER_ID, null, this);

        // Handle no network connectivity
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        return new StoryLoader(this, searchQuery);
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {
        if (loader != null && !stories.isEmpty()) {
            storyAdapter.addAll(stories);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        storyAdapter.clear();
    }
}
