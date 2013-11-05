package com.togastudios.android.toga;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.parse.ParseUser;

import java.util.Calendar;

public class CreatePartyFragment extends Fragment {

    private static final String TAG = "CreatePartyFragment";
    private static final String DIALOG_LOCATION =
            "com.togasoftware.android.toga.dialog_location";

    public static final int REQUEST_LOCATION = 0;
    public static final int REQUEST_THEME = 1;

    private EditText mTitleField;
    private EditText mLocationField;
    private EditText mDescriptionField;
    private Button mDateButton;
    private Button mTimeButton;
    private RadioButton mPublic;
    private RadioButton mPrivate;
    private RadioButton mVip;
    private FrameLayout mThemeLayout;
    private ImageView mThemeImage;

    private Party mParty;
    private Calendar mDate;
    private FragmentManager fm;

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * Creates a new party and initializes its fields.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        fm = getActivity().getSupportFragmentManager();

        // Create new party
        mParty = new Party();
        mParty.initParty();
        ParseUser current = ParseUser.getCurrentUser();
        if (current != null) {
            mParty.setHost(current);
            mParty.setSchool(current.getString("school"));
        }
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
        View v = inflater.inflate(R.layout.fragment_create_party, container, false);
        assert v != null;

        // Obtain handles to UI objects
        mThemeImage = (ImageView) v.findViewById(R.id.create_party_theme_image);
        mThemeLayout = (FrameLayout) v.findViewById(R.id.create_party_theme_layout_background);
        mTitleField = (EditText) v.findViewById(R.id.create_party_title_editText);
        mLocationField = (EditText) v.findViewById(R.id.create_party_location_editText);
        mDescriptionField = (EditText) v.findViewById(R.id.create_party_description_editText);

        // Set theme image resource
        mThemeImage.setImageResource(PhotoBuilder.getThemeResource(mParty.getThemeId()));

        // Register handler for UI elements
        mThemeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeThemeClicked();
            }
        });
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Set party title
                mParty.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing
            }
        });
        mLocationField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationFieldClicked();
            }
        });
        mDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Set description
                mParty.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing
            }
        });

        // Setup helper functions
        setRadioGroup(v);
        setDateAndTime(v);
        setActionBarDone();

        return v;
    }

    /**
     * Receive the result from a previous call to startActivityForResult().
     * @param requestCode   The request code supplied to startActivityForResult(), allowing
     *                      identification of who this result came from.
     * @param resultCode    The result code returned by the child activity through its setResult()
     * @param data          Intent which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If results is not ok, return and do nothing
        if (resultCode != Activity.RESULT_OK) return;

        // Switch on where return came from
        switch (requestCode) {
            case REQUEST_LOCATION:
                // Get returned data
                String locationString = data.getStringExtra(LocationPickerFragment.EXTRA_STRING_LOC);
                Location location = data.getParcelableExtra(LocationPickerFragment.EXTRA_LOCATION);

                if (locationString == null || locationString.equals("")) {
                    // Clears out stored location data in mParty
                    Location clearLocation = new Location(TAG + ": Clear Location");
                    clearLocation.setLatitude(0);
                    clearLocation.setLongitude(0);

                    // Set party fields
                    mParty.setLocation(clearLocation);
                    mParty.setLocationString("");

                    // Set location edit text
                    mLocationField.setText("");
                } else {
                    // Set party fields with return data
                    mParty.setLocation(location);
                    mParty.setLocationString(locationString);
                    mParty.saveInBackground();

                    // Set location edit text
                    mLocationField.setText(locationString);
                }
                break;

            case REQUEST_THEME:
                // Sets the theme to return data
                mParty.setThemeId(data.getIntExtra(ThemePickerFragment.EXTRA_THEME, 0));

                // Sets image to the returned image
                mThemeImage.setImageResource(PhotoBuilder.getThemeResource(mParty.getThemeId()));
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Sets up fragment to use DONE action bar.
     */
    private void setActionBarDone() {
        // Inflate a "Done/Cancel" custom action bar view.
        final LayoutInflater inflater = (LayoutInflater) ((CreatePartyActivity)getActivity()).
                getSupportActionBar().getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_cancel, null);
        assert customActionBarView != null;
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        saveData();
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Cancel"
                        getActivity().finish();
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = ((CreatePartyActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * Sets up radio group for choosing party type.
     * @param v View which RadioButtons are contained in
     */
    private void setRadioGroup(View v) {
        // Obtain handles to RadioButtons
        mPublic = (RadioButton) v.findViewById(R.id.create_party_radio_public);
        mPrivate = (RadioButton) v.findViewById(R.id.create_party_radio_private);
        mVip = (RadioButton) v.findViewById(R.id.create_party_radio_vip);

        // Register handler for RadioButtons
        mPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If this button is the currently checked radio
                    mPrivate.setChecked(false);
                    mVip.setChecked(false);
                    mParty.setType(0);
                }
            }
        });
        mPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If this button is the currently checked radio
                    mPublic.setChecked(false);
                    mVip.setChecked(false);
                    mParty.setType(1);
                }
            }
        });
        mVip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If this button is the currently checked radio
                    mPrivate.setChecked(false);
                    mPublic.setChecked(false);
                    mParty.setType(2);
                }
            }
        });
    }

    /**
     * Sets up the date and time buttons with proper information
     * @param v View which date and time Buttons are contained in
     */
    private void setDateAndTime(View v) {
        // Sets up date and time
        mDateButton = (Button) v.findViewById(R.id.create_party_date_button);
        mTimeButton = (Button) v.findViewById(R.id.create_party_time_button);

        // Sets date to today at 6 pm
        int defaultHour = 6;
        int defaultMinute = 0;
        mDate = Calendar.getInstance();
        mDate.set(mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH),
                mDate.get(Calendar.DAY_OF_MONTH), defaultHour, defaultMinute);

        // Sets date of party and buttons to mDate
        mParty.setDate(mDate.getTime());
        mDateButton.setText(DateFormat.format("MMMM dd, yyyy", mDate));
        mTimeButton.setText(DateFormat.format("hh:mm a", mDate));

        // Register handler for date and time buttons
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Date dialog
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                        mDate.set(year, monthOfYear, dayOfMonth);
                        mParty.setDate(mDate.getTime());
                        mDateButton.setText(DateFormat.format("MMMM dd, yyyy", mDate));
                    }
                }, mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show(getActivity().getSupportFragmentManager(),
                        getResources().getString(R.string.create_party_date_dialog));
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Time dialog
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                        mDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mDate.set(Calendar.MINUTE, minute);
                        mParty.setDate(mDate.getTime());
                        mTimeButton.setText(DateFormat.format("hh:mm a", mDate));
                    }
                }, mDate.get(Calendar.HOUR_OF_DAY), mDate.get(Calendar.MINUTE), false);
                timePickerDialog.show(getActivity().getSupportFragmentManager(),
                        getResources().getString(R.string.create_party_time_dialog));
            }
        });
    }

    /**
     * Saves data and finishes activity.
     */
    private void saveData() {
        // Checks to make sure data is all valid
        if(!checkDataField())
            return;

        // Save party
        mParty.saveInBackground();

        // Return
        getActivity().finish();
    }

    /**
     * Launches the ThemePickerActivity to allow for the choice of party themes.
     */
    private void changeThemeClicked() {
        Intent i = new Intent(getActivity(), ThemePickerActivity.class);
        startActivityForResult(i, REQUEST_THEME);
    }

    /**
     * Launches the LocationPickerDialog to allow for the choice of party location.
     */
    private void locationFieldClicked() {
        // Gets currently saved location and location string
        Location geoPoint = mParty.getLocation();
        String locString = mParty.getLocationString();

        // Creates a new LocationPicker instance based off current location
        LocationPickerFragment locationDialog;
        if (geoPoint == null || locString == null) {
            locationDialog = LocationPickerFragment.newInstance(null, null);
        } else {
            locationDialog = LocationPickerFragment.newInstance(locString, geoPoint);
        }

        // Launch location picker dialog
        locationDialog.setTargetFragment(CreatePartyFragment.this, REQUEST_LOCATION);
        locationDialog.show(fm, DIALOG_LOCATION);
    }

    /**
     * Checks each party field to make sure they have valid data.
     * @return false if invalid field, true if all fields valid
     */
    private boolean checkDataField() {
        // Check title
        if (mParty.getTitle() == null || mParty.getTitle().equals("")) {
            Toast.makeText(getActivity(), "Please enter a title.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check type
        if (mParty.getType() == -1) {
            Toast.makeText(getActivity(), "Please give your party a type.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check location
        if (mParty.getLocation() == null || mParty.getLocationString() == null || mParty.getLocationString().equals("")) {
            Toast.makeText(getActivity(), "Please enter a location.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
