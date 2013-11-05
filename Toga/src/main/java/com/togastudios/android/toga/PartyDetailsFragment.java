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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class PartyDetailsFragment extends Fragment {

    private static final String TAG = "PartyDetailsFragment";
    public static final String EXTRA_PARTY = "com.togasoftware.android.toga.partyDetailsFragment.party";
    public static final String EXTRA_NOW = "com.togasoftware.android.toga.partyDetailsFragment.now";

    private LinearLayout mProgressBar;
    private ScrollView mContent;
    private EditText mTitleField;
    private EditText mLocationField;
    private EditText mDescriptionField;
    private Button mDateButton;
    private Button mTimeButton;
    private RadioButton mPublic;
    private RadioButton mPrivate;
    private RadioButton mVip;
    private ImageView mClickThemeImage;

    private Party mParty;
    private boolean partyNow;

    /**
     * Creates a new instance of the PartyDetailsFragment with arguments set.
     * @param partyID String containing the Party Id
     * @return  PartyDetailsFragment with arguments set
     */
    public static Fragment newInstance(String partyID) {
        Bundle args = new Bundle();
        args.putString(EXTRA_PARTY, partyID);

        PartyDetailsFragment fragment = new PartyDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * Determines if in "Party Now" mode by receiving at fragment argument.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        // Determines if detail is in "Party Now" mode
        partyNow = EXTRA_NOW.equals(getArguments().getString(EXTRA_PARTY));
    }

    /**
     * Called when the fragment is first created. Responsible for initializing the UI.
     * @param inflater  The LayoutInflater object that can be used to inflate views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_party_details, container, false);
        assert v != null;

        // Sets up button if not in "Party Now" mode
        if (!partyNow)
            ((PartyDetailsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain handles to UI objects
        mClickThemeImage = (ImageView) v.findViewById(R.id.party_details_theme);
        mProgressBar = (LinearLayout) v.findViewById(R.id.party_details_downloadProgress);
        mContent = (ScrollView) v.findViewById(R.id.party_details_content);
        mTitleField = (EditText) v.findViewById(R.id.party_details_title_editText);
        mLocationField = (EditText) v.findViewById(R.id.party_details_location_editText);
        mDescriptionField = (EditText) v.findViewById(R.id.party_details_description_editText);
        mDateButton = (Button) v.findViewById(R.id.party_details_date_button);
        mTimeButton = (Button) v.findViewById(R.id.party_details_time_button);
        mPublic = (RadioButton) v.findViewById(R.id.party_details_radio_public);
        mPrivate = (RadioButton) v.findViewById(R.id.party_details_radio_private);
        mVip = (RadioButton) v.findViewById(R.id.party_details_radio_vip);

        return v;
    }

    /**
     * Called after the fragment view first created. Responsible for initializing data based
     * on the UI.
     * @param view The View returned by onCreateView
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPartyDetails();
    }

    /**
     * Inflate menu for the fragment once the options menu has been created.
     * @param menu The options menu in which items are placed
     * @param inflater The MenuInflater object that can be used to inflate menu items in the menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflates menu if not in "Party Now"
        if (!partyNow)
            inflater.inflate(R.menu.party_details, menu);
    }

    /**
     * Handle menu item selection interactions.
     * @param item The menu item that was selected
     * @return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Up button
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            case R.id.action_menu_navigate:
                // Navigate button launches GoogleMaps Navigate
                if (mParty.getLocation() == null) {
                    return false;
                }

                String url = "http://maps.google.com/maps?daddr="+mParty.getLocation().getLatitude()+
                        ","+mParty.getLocation().getLongitude()+"&mode=walking";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;

            case R.id.action_menu_edit_party:
                // TODO implement edit party
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Download party matching the Party Id.
     */
    private void getPartyDetails() {
        // Sets the progress bar to spin until query is done
        mProgressBar.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);

        // Queries for the parties with the specified id or soonest if "Party Now"
        ParseQuery<Party> query = ParseQuery.getQuery(Party.class);

        if (partyNow) {
            query.addAscendingOrder("date");
        } else {
            query.whereEqualTo(Party.idKey, getArguments().getString(EXTRA_PARTY));
        }

        query.getFirstInBackground(new GetCallback<Party>() {
            public void done(Party party, ParseException e) {
                if (getActivity() == null) return;

                if (e == null) {

                    // Remove progress bar
                    mProgressBar.setVisibility(View.GONE);
                    mContent.setVisibility(View.VISIBLE);

                    // Set current party to the returned party
                    mParty = party;

                    // Set action bar title if not in "Party Now" mode
                    if (!partyNow)
                        ((PartyDetailsActivity)getActivity()).getSupportActionBar().setTitle(mParty.getTitle());

                    // Set UI element data
                    mTitleField.setText(mParty.getTitle());
                    mClickThemeImage.setImageResource(PhotoBuilder.getThemeResource(mParty.getThemeId()));
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
