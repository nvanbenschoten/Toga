package com.togastudios.android.toga;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class PartyDetailsFragment extends Fragment {
    private static final String TAG = "PartyDetailsFragment";

    public static String EXTRA_PARTY = "com.togasoftware.android.toga.extraparty";

    public static String EXTRA_NOW = "com.togasoftware.android.toga.now";

    private Party mParty;
    private LinearLayout mProgressBar;
    private FrameLayout mContent;
    private EditText mTitleField;
    private EditText mLocationField;
    private EditText mDescriptionField;
    private Button mDateButton;
    private Button mTimeButton;
    private RadioButton mPublic;
    private RadioButton mPrivate;
    private RadioButton mVip;
    private ImageView mClickThemeImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_party_details, container, false);

        if (!getArguments().getString(EXTRA_PARTY).equals(EXTRA_NOW))
            ((PartyDetailsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClickThemeImage = (ImageView)v.findViewById(R.id.party_details_theme);

        mProgressBar = (LinearLayout)v.findViewById(R.id.party_details_downloadProgress);

        mContent = (FrameLayout)v.findViewById(R.id.party_details_content);

        mTitleField = (EditText)v.findViewById(R.id.party_details_title_editText);

        mLocationField = (EditText)v.findViewById(R.id.party_details_location_editText);

        mDescriptionField = (EditText)v.findViewById(R.id.party_details_description_editText);

        mDateButton = (Button)v.findViewById(R.id.party_details_date_button);
        mTimeButton = (Button)v.findViewById(R.id.party_details_time_button);

        mPublic = (RadioButton)v.findViewById(R.id.party_details_radio_public);
        mPrivate = (RadioButton)v.findViewById(R.id.party_details_radio_private);
        mVip = (RadioButton)v.findViewById(R.id.party_details_radio_vip);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getHostedParty();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!getArguments().getString(EXTRA_PARTY).equals(EXTRA_NOW))
            inflater.inflate(R.menu.party_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.action_menu_navigate:

                if (mParty.getLocation() == null) {
                    return false;
                }

                String url = "http://maps.google.com/maps?daddr="+mParty.getLocation().getLatitude()+","+mParty.getLocation().getLongitude()+"&mode=walking";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                //intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);


                return true;
            case R.id.action_menu_edit_party:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Fragment newInstance(String partyID) {
        Bundle args = new Bundle();
        args.putString(EXTRA_PARTY, partyID);

        PartyDetailsFragment fragment = new PartyDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void getHostedParty() {
        // Get the parties the host is hosting
        mProgressBar.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        ParseQuery<Party> query = ParseQuery.getQuery(Party.class);

        if (getArguments().getString(EXTRA_PARTY).equals(EXTRA_NOW)) {
            query.addAscendingOrder("date");
        } else {
            query.whereEqualTo(Party.idKey, getArguments().getString(EXTRA_PARTY));
        }

        query.getFirstInBackground(new GetCallback<Party>() {
            public void done(Party party, ParseException e) {
                if (e == null) {
                    mProgressBar.setVisibility(View.GONE);
                    mContent.setVisibility(View.VISIBLE);
                    mParty = party;

                    mTitleField.setText(mParty.getTitle().toString());
                    if (!getArguments().getString(EXTRA_PARTY).equals(EXTRA_NOW))
                        ((PartyDetailsActivity)getActivity()).getSupportActionBar().setTitle(mParty.getTitle());

                    mClickThemeImage.setImageResource(ThemePhotoBuilder.getThemeResource(mParty.getThemeId()));

                    mLocationField.setText(mParty.getLocationString());

                    if (mParty.getDescription() == null || mParty.getDescription().equals(""))
                        mDescriptionField.setVisibility(View.INVISIBLE);
                    else
                        mDescriptionField.setText(mParty.getDescription());

                    mDateButton.setText(DateFormat.format("MMMM dd, yyyy", mParty.getDate()));
                    mTimeButton.setText(DateFormat.format("hh:mm a", mParty.getDate()));

                    switch (mParty.getType()) {
                        case 0:
                            mPublic.setChecked(true);
                            break;
                        case 1:
                            mPrivate.setChecked(true);
                            break;
                        case 2:
                            mVip.setChecked(true);
                            break;
                    }


                } else {
                    Log.d(TAG, "Could not retrieve the party");
                }
            }
        });
    }
}
