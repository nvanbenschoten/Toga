package com.togastudios.android.toga;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PartyCardAdapter extends ArrayAdapter<Party> {

    Typeface mRobotoThin;
    ImageView mIconImageView;
    ImageView mBigImageView;

    public PartyCardAdapter(Context context, ArrayList<Party> parties) {
        super(context, 0, parties);
        mRobotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-thin.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Not given a view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.card_list_item, null);
        }

        // Configure view for this party
        Party party = getItem(position);

        // Set theme image
        mBigImageView = (ImageView)convertView.findViewById(R.id.cardImage);
        mBigImageView.setImageResource(ThemePhotoBuilder.getThemeResource(party.getThemeId()));

        // Set icon image
        mIconImageView = (ImageView)convertView.findViewById(android.R.id.icon);
        String school = party.getSchool();
        setImage(school);

        // Set title
        TextView titleTextView = (TextView)convertView.findViewById(android.R.id.title);
        titleTextView.setText(party.getTitle());

        // Set details
        TextView contentTextView = (TextView)convertView.findViewById(android.R.id.content);
        contentTextView.setText(party.getLocationString());

        //ImageButton optionButton = (ImageButton)convertView.findViewById(android.R.id.button1);

        return convertView;
    }

    private void setImage(String school) {
        if (school.equals("Northeastern"))
            mIconImageView.setImageResource(R.drawable.logo_northeastern);
        else if (school.equals("MIT"))
            mIconImageView.setImageResource(R.drawable.logo_mit);
        else if (school.equals("Boston University"))
            mIconImageView.setImageResource(R.drawable.logo_bu);
        else if (school.equals("Harvard"))
            mIconImageView.setImageResource(R.drawable.logo_harvard);
        else
            mIconImageView.setImageResource(R.drawable.ic_launcher);
    }

}
