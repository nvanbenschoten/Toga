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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class LocationPickerFragment extends DialogFragment {

    private static final String TAG = "LocationPickerFragment";
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

    /**
     * Creates a new instance of the LocationPickerFragment dialog with arguments set.
     * @param locationString Previous location as a string
     * @param location  Previous location
     * @return  LocationPickerFragment with arguments set
     */
    public static LocationPickerFragment newInstance(String locationString, Location location) {
        // Sets args
        Bundle args = new Bundle();
        args.putString(EXTRA_STRING_LOC, locationString);
        args.putParcelable(EXTRA_LOCATION, location);

        // Create new fragment
        LocationPickerFragment fragment = new LocationPickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_location_picker, null);
        assert v != null;

        // Make sure geocoder is available and gain handle to it
        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(), "Geocoder not available.", Toast.LENGTH_LONG).show();
            getDialog().dismiss();
        }
        mGeocoder = new Geocoder(getActivity());

        // Get fragment arguments
        mLocationString = getArguments().getString(EXTRA_STRING_LOC);
        mLocation = getArguments().getParcelable(EXTRA_LOCATION);

        // Obtain handles to UI objects
        mEditText = (EditText) v.findViewById(R.id.location_picker_edittext);
        mMyLocationButton = (ImageButton) v.findViewById(R.id.location_picker_button);

        // Register handler for UI elements
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Set fields
                changed = true;
                useMyLocation = false;
                mEditText.setTextColor(Color.BLACK);

                // Set string to s
                mLocationString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocationButton();
            }
        });

        // Checks passed in arguments and handles acordingly
        if (mLocationString != null && mLocation != null) {
            // Set changed to false and set edit text
            changed = false;
            mEditText.setText(mLocationString);
        } else {
            // Set changed to true and init mLocationString
            changed = true;
            mLocationString = "";
            mLocation = new Location("LocationPickerFragment");
        }

        // Set location listener
        setupLocationListener();

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
                    }
                })
                .create();
    }

    /**
     * Sets up location listener to handle changes in users location.
     */
    private void setupLocationListener() {
        // Set use my location to false and disable the button
        useMyLocation = false;
        mMyLocationButton.setEnabled(false);

        // Get hold of location manager
        LocationManager locationManager = (LocationManager) getActivity().
                getSystemService(Context.LOCATION_SERVICE);

        // Request location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Set current location and enable location button
                Log.d(TAG, "Location recieved");
                mCurrentLocation = location;
                mMyLocationButton.setEnabled(true);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        });
    }

    /**
     * Handles the dialog's positive button action. Sets result code and returns intent with
     * geocode location if needed.
     */
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
                        Toast.makeText(getActivity(), "Could not resolve location.",
                                Toast.LENGTH_LONG).show();
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

    /**
     * Handles the MyLocation button action. Sets location to current location.
     */
    private void myLocationButton() {
        // Set changed and use my location
        changed = true;
        useMyLocation = true;
        mEditText.setText("My Location");
        mEditText.setTextColor(getResources().getColor(R.color.pressed_toga));

        // location field is set to my location
        mLocation = mCurrentLocation;
        mLocationString = "";

        // Try to set the location string
        try {
            List<Address> addresses = mGeocoder.getFromLocation(mLocation.getLatitude(),
                    mLocation.getLongitude(), 1);

            if (addresses.size() == 0) {
                Toast.makeText(getActivity(), "Could not resolve location.", Toast.LENGTH_LONG).show();
            } else {
                // Adds geocode location string to locationString
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
