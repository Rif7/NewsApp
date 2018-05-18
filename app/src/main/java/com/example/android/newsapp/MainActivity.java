package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set adapter fot ListView
        // Set an item click listener on the ListView
        // Fetch data
            // Initialize loader
        // Handle no network connectivity
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {

    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {

    }
}
