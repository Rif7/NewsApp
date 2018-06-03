package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Story>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static String searchQuery = null;
    private StoryAdapter storyAdapter;
    private TextView emptyListViewMessage;
    private ProgressBar loadingIndicator;

    private final int STORY_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set adapter fot ListView
        ListView storyListView = findViewById(R.id.main_list);
        storyAdapter = new StoryAdapter(this, new ArrayList<Story>());
        storyListView.setAdapter(storyAdapter);

        // Obtain a reference to the SharedPreferences file for this app register preference changes
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Set View for no elements in list
        emptyListViewMessage = findViewById(R.id.main_empty_view);
        storyListView.setEmptyView(emptyListViewMessage);

        // Set View for no elements in list
        loadingIndicator = findViewById(R.id.main_loading_indicator);

        // Set an item click listener on the ListView
        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(storyAdapter.getItem(position).getWebUrl()));

                startActivity(websiteIntent);
            }
        });

        // Search new information
        SearchView newStorySearchView = findViewById(R.id.main_search);
        if (searchQuery == null) {
            searchQuery = getString(R.string.initial_search);
        } else {
            newStorySearchView.setQuery( searchQuery, false);
        }
        newStorySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchQuery = s;
                populateList(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });
        populateList(false);
    }

    private void populateList(boolean isRestart) {
        // Handle no network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Initialize loader to fetch data
            LoaderManager loaderManager = getLoaderManager();
            if (!isRestart) {
                loaderManager.initLoader(STORY_LOADER_ID, null, this);
            } else {
                getLoaderManager().restartLoader(STORY_LOADER_ID, null, this);
            }
        } else {
            emptyListViewMessage.setText(R.string.no_internet_connection);
            if (isRestart) {
                Toast connectionBroken = Toast.makeText(getApplicationContext(),
                        R.string.lost_internet_connection, Toast.LENGTH_LONG);
                connectionBroken.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        loadingIndicator.setVisibility(View.VISIBLE);
        return new StoryLoader(this, searchQuery);
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {
        storyAdapter.clear();
        loadingIndicator.setVisibility(View.GONE);

        if (stories == null) {
            // Connection Error
            emptyListViewMessage.setText(R.string.error_info);
        } else if((loader != null && !stories.isEmpty())) {
            emptyListViewMessage.setText(String.format("%s: %s", getString(R.string.no_articles_with), searchQuery));
            storyAdapter.addAll(stories);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        storyAdapter.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_show_images_key)) ||
                key.equals(getString(R.string.settings_page_size_key)) ||
                key.equals(getString(R.string.settings_show_body_key)) ||
                key.equals(getString(R.string.settings_order_by_key))) {

            storyAdapter.clear();

            emptyListViewMessage.setVisibility(View.GONE);

            View loadingIndicator = findViewById(R.id.main_loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(STORY_LOADER_ID, null, this);
        }
    }
}
