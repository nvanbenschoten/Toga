package com.togastudios.android.toga;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class LocationPickerFragment extends DialogFragment {

    public static final String EXTRA_STRING_LOC =
            "com.togastudios.android.toga.location_picker_string";
    public static final String EXTRA_LOCATION =
            "com.togastudios.android.toga.location_picker_location";

    private EditText mEditText;
    private ImageButton mMyLocationButton;
    private String mLocationString;
    private Location mLocation;

    private Geocoder mGeocoder;
    private Location mCurrentLocation;

    private boolean useMyLocation;
    private boolean changed;

    public static LocationPickerFragment newInstance(String locationString, Location location) {
        // Sets args
        Bundle args = new Bundle();
        args.putString(EXTRA_STRING_LOC, locationString);
        args.putParcelable(EXTRA_LOCATION, location);

        LocationPickerFragment fragment = new LocationPickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_location_picker, null);

        // Get decoder
        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(), "Geocoder not available.", Toast.LENGTH_LONG).show();
            getDialog().dismiss();
        }
        mGeocoder = new Geocoder(getActivity());

        // Setup edit text with listener
        mEditText = (EditText)v.findViewById(R.id.location_picker_edittext);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changed = true;
                useMyLocation = false;
                mEditText.setTextColor(Color.BLACK);

                mLocationString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Setup my location button
        mMyLocationButton = (ImageButton)v.findViewById(R.id.location_picker_button);
        mMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocationButton();
            }
        });

        // Get arguments
        mLocationString = getArguments().getString(EXTRA_STRING_LOC);
        mLocation = getArguments().getParcelable(EXTRA_LOCATION);

        // If passed in legit data
        if (mLocationString != null && mLocation != null) {
            // Set changed tp false and set edittext
            changed = false;
            mEditText.setText(mLocationString);
        } else {
            // Set changed to true and init mLocationString
            changed = true;
            mLocationString = "";
            mLocation = new Location("LocationPickerFragment");
        }

        setupListener();

        // Return alert dialog
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(getString(R.string.party_location))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        positiveButton();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO
                    }
                })
                .create();
    }

    private void setupListener() {
        // Set use my location to false and disable the button
        useMyLocation = false;
        mMyLocationButton.setEnabled(false);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Set current location and enable location button
                mCurrentLocation = location;
                mMyLocationButton.setEnabled(true);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }

    private void positiveButton() {
        // Returns if no target fragment
        if (getTargetFragment() == null) {
            return;
        }

        // Creates intent with extras
        Intent i = new Intent();
        i.putExtra(EXTRA_STRING_LOC, mLocationString);
        i.putExtra(EXTRA_LOCATION, mLocation);
        int resultCode = Activity.RESULT_OK;


        if (!changed) {
            // If not changed, return canceled
            resultCode = Activity.RESULT_CANCELED;
        }
        else if (useMyLocation) {
            // If using my location, all set up already
        }
        else {
            if (mLocationString.length() == 0 || mLocationString.equals("")) {
                // If location string is blank, cancel

            } else {
                // Else, try to geocode
                try {
                    List<Address> addresses = mGeocoder.getFromLocationName(mLocationString, 1);

                    if (addresses == null || addresses.size() == 0) {
                        // Result of geocode bad
                        Toast.makeText(getActivity(), "Could not resolve location.", Toast.LENGTH_LONG).show();
                        resultCode = Activity.RESULT_CANCELED;
                    } else {
                        // Result good
                        mLocation.setLatitude(addresses.get(0).getLatitude());
                        mLocation.setLongitude(addresses.get(0).getLongitude());
                        i.putExtra(EXTRA_LOCATION, mLocation);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    resultCode = Activity.RESULT_CANCELED;
                }
            }
        }

        // Return
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    private void myLocationButton() {
        // Set changed and use my location
        changed = true;
        useMyLocation = true;
        mEditText.setText("My Location");
        mEditText.setTextColor(getResources().getColor(R.color.pressed_toga));

        mLocation = mCurrentLocation;
        mLocationString = "";

        // Try to set the location string
        try {
            List<Address> addresses = mGeocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);

            if (addresses.size() == 0) {
                Toast.makeText(getActivity(), "Could not resolve location.", Toast.LENGTH_LONG).show();
            } else {
                for (int i=0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                    if (!(i==0))
                        mLocationString += "  ";
                    mLocationString += addresses.get(0).getAddressLine(i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
