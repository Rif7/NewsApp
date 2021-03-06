package com.example.android.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class StoryAdapter extends ArrayAdapter<Story> {
    private final ArrayList<String> sections;

    public StoryAdapter(Context context, List<Story> stories) {
        super(context, 0, stories);
        sections = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.story_item, parent, false);
        }

        Story story = getItem(position);

        View dateView = convertView.findViewById(R.id.story_item_data);
        Date publicationDate = story.getWebPublicationDate();
        if (publicationDate != null) {
            TextView dayMonthView = convertView.findViewById(R.id.story_item_data_day_month);
            dayMonthView.setText(formatDayMonth(publicationDate));
            TextView yearView = convertView.findViewById(R.id.story_item_data_year);
            yearView.setText(formatYear(publicationDate));
        } else {
            dateView.setVisibility(View.GONE);
        }

        TextView sectionNameView = convertView.findViewById(R.id.story_item_section_name);
        sectionNameView.setText(story.getSectionName());
        if (!sections.contains(story.getSectionName())) {
            sections.add(story.getSectionName());
        }
        sectionNameView.setBackgroundColor(getSectionColor(sections.indexOf(story.getSectionName())));

        TextView webTitleView = convertView.findViewById(R.id.story_item_web_title);
        webTitleView.setText(story.getWebTitle());

        ArrayList<String> authors = story.getAuthors();
        TextView authorsView = convertView.findViewById(R.id.story_item_authors);
        if (authors != null) {
            authorsView.setText(Arrays.toString(authors.toArray()).replaceAll("[\\[\\]]", ""));
        } else {
            authorsView.setVisibility(View.GONE);
        }

        // Set image view and depending on the image visibility set title position
        Bitmap image = story.getImage();
        ImageView imageView = convertView.findViewById(R.id.story_item_image);
        RelativeLayout.LayoutParams webTitleViewPositionParameters =
                (RelativeLayout.LayoutParams) webTitleView.getLayoutParams();
        if(image != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(image);
            webTitleViewPositionParameters.addRule(RelativeLayout.BELOW, imageView.getId());

        } else {
            imageView.setVisibility(View.GONE);
            webTitleViewPositionParameters.addRule(RelativeLayout.END_OF, dateView.getId());
        }

        TextView bodyTextView = convertView.findViewById(R.id.story_item_body_text);
        String bodyText = story.getBodyText();
        if (bodyText != null) {
            bodyText = trimBodyText(bodyText);
            bodyTextView.setVisibility(View.VISIBLE);
            bodyTextView.setText(bodyText);
        } else {
            bodyTextView.setVisibility(View.GONE);
        }

        return convertView;
    }

    private String formatDayMonth(Date dateObject) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL");
        return dateFormat.format(dateObject);
    }

    private String formatYear(Date dateObject) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy");
        return timeFormat.format(dateObject);
    }

    private String trimBodyText(String bodyText) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        int bodyTextLength = Integer.parseInt(sharedPrefs.getString(
                getContext().getString(R.string.settings_show_body_key),
                getContext().getString(R.string.settings_show_body_default)));

        if (bodyTextLength < bodyText.length() && bodyTextLength !=
                Integer.parseInt(getContext().getString(R.string.settings_show_body_all_value))) {
            bodyText = bodyText.substring(0, bodyTextLength) + "...";
        }
        return bodyText;
    }

    @Override
    public void clear() {
        super.clear();
        sections.clear();
    }

    private int getSectionColor(int position) {
        int sectionColorId;
        switch (position) {
            case 0:
                sectionColorId = R.color.section0;
                break;
            case 1:
                sectionColorId = R.color.section1;
                break;
            case 2:
                sectionColorId = R.color.section2;
                break;
            case 3:
                sectionColorId = R.color.section3;
                break;
            case 4:
                sectionColorId = R.color.section4;
                break;
            case 5:
                sectionColorId = R.color.section5;
                break;
            case 6:
                sectionColorId = R.color.section6;
                break;
            case 7:
                sectionColorId = R.color.section7;
                break;
            case 8:
                sectionColorId = R.color.section8;
                break;
            case 9:
                sectionColorId = R.color.section9;
                break;
            default:
                sectionColorId = R.color.section_default;
                break;
        }
        return ContextCompat.getColor(getContext(), sectionColorId);
    }

}
