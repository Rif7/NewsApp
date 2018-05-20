package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class StoryLoader extends AsyncTaskLoader<List<Story>> {
    private String searchQuery;

    public StoryLoader(Context context, String searchQuery) {
        super(context);
        this.searchQuery = searchQuery;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Story> loadInBackground() {
        return QueryUtils.prepareNews(searchQuery);
    }
}