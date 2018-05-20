package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Story>> {
    private static int STORY_LOADER_ID = 1;

    private String searchQuery = "Royal Wedding";
    private StoryAdapter storyAdapter;
    private TextView emptyListViewMessage;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set adapter fot ListView
        ListView storyListView = (ListView) findViewById(R.id.list);
        storyAdapter = new StoryAdapter(this, new ArrayList<Story>());
        storyListView.setAdapter(storyAdapter);

        // Set View for no elements in list
        emptyListViewMessage = (TextView) findViewById(R.id.empty_view);
        storyListView.setEmptyView(emptyListViewMessage);

        // Set View for no elements in list
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        // Set an item click listener on the ListView
        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(storyAdapter.getItem(position).getWebUrl()));

                startActivity(websiteIntent);
            }
        });

        // Handle no network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Initialize loader to fetch data
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(STORY_LOADER_ID, null, this);
        } else {
            emptyListViewMessage.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        loadingIndicator.setVisibility(View.VISIBLE);
        storyAdapter.clear();
        return new StoryLoader(this, searchQuery);
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {
        loadingIndicator.setVisibility(View.GONE);

        emptyListViewMessage.setText(String.format("%s: %s", getString(R.string.no_articles_with), searchQuery));

        if (loader != null && !stories.isEmpty()) {
            storyAdapter.addAll(stories);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        storyAdapter.clear();
    }
}
