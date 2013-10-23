package com.togastudios.android.toga;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        mBigImageView = (ImageView)convertView.findViewById(R.id.cardImage);
        if (position%2 == 0)
            mBigImageView.setImageResource(R.drawable.black_light_party);
        else
            mBigImageView.setImageResource(R.drawable.party_frat_house);

        mIconImageView = (ImageView)convertView.findViewById(android.R.id.icon);
        String school = party.getSchool();
        setImage(school);

        TextView titleTextView = (TextView)convertView.findViewById(android.R.id.title);
        titleTextView.setText(party.getTitle());

        TextView contentTextView = (TextView)convertView.findViewById(android.R.id.content);
        contentTextView.setText(party.getLocationString());

        //ImageButton optionButton = (ImageButton)convertView.findViewById(android.R.id.button1);

        //setImageTouchListeners(convertView);
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

    private void setImageTouchListeners(View convertView) {
        LinearLayout cardListItemLayout = (LinearLayout)convertView.findViewById(R.id.card_list_item_layout);

        cardListItemLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //overlay is black with transparency of 0x77 (119)
                        mBigImageView.getDrawable().setColorFilter(0x77849E69, PorterDuff.Mode.SRC_ATOP);
                        mBigImageView.invalidate();

                        mIconImageView.getDrawable().setColorFilter(0x77849E69, PorterDuff.Mode.SRC_ATOP);
                        mIconImageView.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        mBigImageView.getDrawable().clearColorFilter();
                        mBigImageView.invalidate();

                        mIconImageView.getDrawable().clearColorFilter();
                        mIconImageView.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
    }
}
