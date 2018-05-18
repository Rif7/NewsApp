package com.example.android.newsapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class StoryAdapter extends ArrayAdapter<Story> {

    public StoryAdapter(Context context, List<Story> stories) {
        super(context, 0, stories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        return listItemView;
    }
}
