package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StoryAdapter extends ArrayAdapter<Story> {

    public StoryAdapter(Context context, List<Story> stories) {
        super(context, 0, stories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.story_item, parent, false);
        }

        Story story = getItem(position);

        View dateView = convertView.findViewById(R.id.story_item_data);
        Date publicationDate = story.getWebPublicationDate();
        if (publicationDate != null) {
            TextView dayMonthView = (TextView) convertView.findViewById(R.id.story_item_data_day_month);
            dayMonthView.setText(formatDayMonth(publicationDate));
            TextView yearView = (TextView) convertView.findViewById(R.id.story_item_data_year);
            yearView.setText(formatYear(publicationDate));
        } else {
            dateView.setVisibility(View.GONE);
        }

        TextView sectionNameView = (TextView) convertView.findViewById(R.id.story_item_section_name);
        sectionNameView.setText(story.getSectionName());

        TextView webTitleView = (TextView) convertView.findViewById(R.id.story_item_web_title);
        webTitleView.setText(story.getWebTitle());

        ArrayList<String> authors = story.getAuthors();
        TextView authorsView = (TextView) convertView.findViewById(R.id.story_item_authors);
        if (authors != null) {
            authorsView.setText(Arrays.toString(authors.toArray()).replaceAll("[\\[\\]]", ""));
        } else {
            authorsView.setVisibility(View.GONE);
        }

        return convertView;
    }

    private String formatDayMonth(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL");
        return dateFormat.format(dateObject);
    }

    private String formatYear(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy");
        return timeFormat.format(dateObject);
    }

}
