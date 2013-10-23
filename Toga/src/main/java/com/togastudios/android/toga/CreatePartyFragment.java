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
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.parse.ParseUser;

import java.util.Calendar;

public class CreatePartyFragment extends Fragment {

    private static final String DIALOG_LOCATION =
            "com.togasoftware.android.toga.dialog_location";

    public static final int REQUEST_LOCATION = 0;

    private Party mParty;
    private EditText mTitleField;
    private EditText mLocationField;
    private EditText mDescriptionField;
    private Button mDateButton;
    private Button mTimeButton;
    private RadioButton mPublic;
    private RadioButton mPrivate;
    private RadioButton mVip;
    private Calendar mDate;

    private FragmentManager fm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        fm = getActivity().getSupportFragmentManager();

        mParty = new Party();
        mParty.initParty();
        ParseUser current = ParseUser.getCurrentUser();
        if (current != null) {
            mParty.setHost(current);
            mParty.setSchool(current.getString("school"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_party, container, false);

        mTitleField = (EditText)v.findViewById(R.id.create_party_title_editText);
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mParty.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing
            }
        });

        mLocationField = (EditText)v.findViewById(R.id.create_party_location_editText);
        mLocationField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location geoPoint = mParty.getLocation();
                String locString = mParty.getLocationString();

                LocationPickerFragment locationDialog;
                if (geoPoint == null || locString == null) {
                    locationDialog = LocationPickerFragment.newInstance(null, null);
                } else {
                    locationDialog = LocationPickerFragment.newInstance(locString, geoPoint);
                }

                locationDialog.setTargetFragment(CreatePartyFragment.this, REQUEST_LOCATION);
                locationDialog.show(fm, DIALOG_LOCATION);
            }
        });

        mDescriptionField = (EditText)v.findViewById(R.id.create_party_description_editText);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_LOCATION:
                // Get returned data
                String locationString = data.getStringExtra(LocationPickerFragment.EXTRA_STRING_LOC);
                Location location = data.getParcelableExtra(LocationPickerFragment.EXTRA_LOCATION);

                if (locationString.equals("")) {
                    // Set location to nothing
                    mLocationField.setText("");

                    // Clears out stored location data in mParty
                    location.setLatitude(0);
                    location.setLongitude(0);

                    mParty.setLocation(location);
                    mParty.setLocationString("");
                } else {
                    // Set party fields
                    mParty.setLocation(location);
                    mParty.setLocationString(locationString);
                    mParty.saveInBackground();

                    // Set edittext text
                    mLocationField.setText(locationString);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void saveData() {
        // Checks to make sure data is all valid
        if(checkDataField())
            return;

        // Save party
        mParty.saveInBackground();

        // Return
        getActivity().finish();
    }

    private void setActionBarDone() {
        // BEGIN_INCLUDE (inflate_set_custom_view)
        // Inflate a "Done/Cancel" custom action bar view.
        final LayoutInflater inflater = (LayoutInflater) ((CreatePartyActivity)getActivity()).
                getSupportActionBar().getThemedContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_cancel, null);
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
        // END_INCLUDE (inflate_set_custom_view)
    }

    private void setRadioGroup(View v) {
        mPublic = (RadioButton)v.findViewById(R.id.radio_public);
        mPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do stuff
                if (isChecked) {
                    mPrivate.setChecked(false);
                    mVip.setChecked(false);
                    mParty.setType(0);
                }
            }
        });

        mPrivate = (RadioButton)v.findViewById(R.id.radio_private);
        mPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do stuff
                if (isChecked) {
                    mPublic.setChecked(false);
                    mVip.setChecked(false);
                    mParty.setType(1);
                }
            }
        });

        mVip = (RadioButton)v.findViewById(R.id.radio_vip);
        mVip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do stuff
                if (isChecked) {
                    mPrivate.setChecked(false);
                    mPublic.setChecked(false);
                    mParty.setType(2);
                }
            }
        });
    }

    private void setDateAndTime(View v) {
        // Sets up date and time
        mDateButton = (Button)v.findViewById(R.id.create_party_date_button);
        mTimeButton = (Button)v.findViewById(R.id.create_party_time_button);

        // Sets date to today at 6 pm
        mDate = Calendar.getInstance();
        mParty.setDate(mDate.getTime());
        mDate.set(mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH), 18, 0);
        setDateButton();
        setTimeButton();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Date dialog
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                        mDate.set(year, monthOfYear, dayOfMonth);
                        mParty.setDate(mDate.getTime());
                        setDateButton();
                    }
                }, mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show(getActivity().getSupportFragmentManager(), "Date");

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
                        setTimeButton();
                    }
                }, mDate.get(Calendar.HOUR_OF_DAY), mDate.get(Calendar.MINUTE), false);
                timePickerDialog.show(getActivity().getSupportFragmentManager(), "Time");
            }
        });
    }

    private void setDateButton() {
        mDateButton.setText(DateFormat.format("MMMM dd, yyyy", mDate));
    }

    private void setTimeButton() {
        mTimeButton.setText(DateFormat.format("hh:mm a", mDate));
    }

    private boolean checkDataField() {
        if (mParty.getTitle() == null || mParty.getTitle().equals("")) {
            Toast.makeText(getActivity(), "Please enter a title.", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (mParty.getType() == -1) {
            Toast.makeText(getActivity(), "Please give your party a type.", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (mParty.getLocation() == null || mParty.getLocationString() == null || mParty.getLocationString().equals("")) {
            Toast.makeText(getActivity(), "Please enter a location.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }
}
