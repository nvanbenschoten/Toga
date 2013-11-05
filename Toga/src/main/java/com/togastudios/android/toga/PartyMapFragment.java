package com.togastudios.android.toga;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class PartyMapFragment extends SupportMapFragment {

    private static final String TAG = "PartyMapFragment";

    private static final double map_default_zoom = 14.5;

    private GoogleMap mMap;
    private HashMap<String, Party> mParties;
    private Location mMyLocation;

    private boolean mFirstLocation;

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Set the first location to true
        mFirstLocation = true;
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
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;

        // Gets handle on map
        mMap = getMap();

        // Creates HashMap between
        mParties = new HashMap<String, Party>();

        // Set map settings and action handlers
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        mMap.setInfoWindowAdapter(new PartyWindowAdapter());
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                infoWindowClicked(marker.getId());
            }
        });

        // Setup location listener
        setupListener();

        // Get parties from backend
        getParties();

        return v;
    }

    /**
     * Sets up location listener to handle changes in users location.
     */
    private void setupListener() {
        // Get hold of location manager
        LocationManager locationManager = (LocationManager) getActivity().
                getSystemService(Context.LOCATION_SERVICE);

        // Request location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mFirstLocation) {
                    // If first location found, move camera
                    CameraPosition cp = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom((float) map_default_zoom)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                    mFirstLocation = false;
                }

                // Record user last known location in field
                mMyLocation = location;
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
     * Populate the map with all parties found in near future.
     */
    private void getParties() {
        // Get the parties near the host
        // TODO add range to search
        ParseQuery<Party> query = ParseQuery.getQuery(Party.class);
        query.findInBackground(new FindCallback<Party>() {
            public void done(List<Party> partyList, ParseException e) {
                if (e == null) {
                    for (Party aPartyList : partyList) {
                        // Add each party to the map
                        Location location = aPartyList.getLocation();
                        String name = aPartyList.getTitle();

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title(name));

                        mParties.put(marker.getId(), aPartyList);
                    }
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Handles info window clicks by launching corresponding PartyDetailsActivity.
     * @param id String id of the party clicked
     */
    private void infoWindowClicked(String id) {
        // Gets party from HashMap
        Party party = mParties.get(id);

        // Starts PartyDetailsActivity with clicked parties id
        Intent i = new Intent(getActivity(), PartyDetailsActivity.class);
        i.putExtra(PartyDetailsFragment.EXTRA_PARTY, party.getId());
        startActivity(i);
    }

    /**
     * Info window adapter which holds party details on map.
     */
    private class PartyWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private static final double miles_per_meter = 0.000621371;

        /**
         * Returns null info window so that it will later be inflated from XML.
         * @param marker Info marker corresponding to the info window.
         * @return View to be shown in info window, or null
         */
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        /**
         * Gets the content for each info window, based on the corresponding marker.
         * @param marker Info marker corresponding to the info window
         * @return View corresponding to the data at the specified marker
         */
        @Override
        public View getInfoContents(Marker marker) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.map_info_window, null);
            if (v == null) return null;

            // Getting the party from the marker
            Party party = mParties.get(marker.getId());

            // Obtain handles to UI objects
            ImageView iconImageView = (ImageView) v.findViewById(android.R.id.icon);
            TextView titleTextView = (TextView) v.findViewById(android.R.id.title);
            TextView contentTextView = (TextView) v.findViewById(android.R.id.content);

            // Set data for UI elements
            PhotoBuilder.setSchoolImage(iconImageView, party.getSchool());
            titleTextView.setText(party.getTitle());

            // Set distance measurement
            double distanceInMiles = mMyLocation.distanceTo(party.getLocation()) * miles_per_meter;
            DecimalFormat df = new DecimalFormat("#.##");
            contentTextView.setText(df.format(distanceInMiles) + " miles away");

            return v;
        }

    }

}
