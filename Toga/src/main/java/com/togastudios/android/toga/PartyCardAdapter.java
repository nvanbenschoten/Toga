package com.togastudios.android.toga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PartyCardAdapter extends ArrayAdapter<Party> {

    /**
     * Public constructor allows for PartyCardAdapter instantiation.
     * @param context Context of invoking activity/ fragment
     * @param parties ArrayList of parties to be adapter to a view
     */
    public PartyCardAdapter(Context context, ArrayList<Party> parties) {
        super(context, 0, parties);
        //Typeface mRobotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-thin.ttf");
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * @param position integer position of view in array
     * @param convertView Old view to reuse, if possible. If not, is null
     * @param parent The parent that this view will eventually be attached to
     * @return View corresponding to the data at the specified position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Not given a view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.card_list_item, null);
        }
        assert convertView != null;

        // Configure view for this party
        Party party = getItem(position);

        // Obtain handles to UI objects
        ImageView mBigImageView = (ImageView) convertView.findViewById(R.id.cardImage);
        ImageView mIconImageView = (ImageView) convertView.findViewById(android.R.id.icon);
        TextView titleTextView = (TextView) convertView.findViewById(android.R.id.title);
        TextView contentTextView = (TextView) convertView.findViewById(android.R.id.content);

        // Set data for UI elements
        titleTextView.setText(party.getTitle());
        contentTextView.setText(party.getLocationString());
        mBigImageView.setImageResource(PhotoBuilder.getThemeResource(party.getThemeId()));
        PhotoBuilder.setSchoolImage(mIconImageView, party.getSchool());

        //ImageButton optionButton = (ImageButton)convertView.findViewById(android.R.id.button1);

        return convertView;
    }

}
