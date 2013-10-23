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

    private GoogleMap mMap;
    private HashMap<String, Party> mParties;
    private Location mMyLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mMap = getMap();

        mParties = new HashMap<String, Party>();

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
                String id = marker.getId();
                Party party = mParties.get(id);

                Intent i = new Intent(getActivity(), PartyDetailsActivity.class);
                i.putExtra(PartyDetailsFragment.EXTRA_PARTY, party.getId());
                startActivity(i);
            }
        });

        setupListener();
        getParties();

        return v;

    }

    private void setupListener() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Toast.makeText(getActivity(), "Moved to " + arg0.toString(), Toast.LENGTH_LONG).show();
                CameraPosition cp = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom((float)14.5)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

                mMyLocation = location;
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

    private void getParties() {
        // Get the parties the host is hosting
        ParseQuery<Party> query = ParseQuery.getQuery(Party.class);
        query.findInBackground(new FindCallback<Party>() {
            public void done(List<Party> partyList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < partyList.size(); i++) {
                        // Add each party to the map
                        Location location = partyList.get(i).getLocation();
                        String name = partyList.get(i).getTitle();

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title(name));

                        mParties.put(marker.getId(), partyList.get(i));

                    }



                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private class PartyWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            // Getting view from the layout file info_window_layout
            View v = getActivity().getLayoutInflater().inflate(R.layout.map_info_window, null);

            // Getting the position from the marker
            String id = marker.getId();

            Party party = mParties.get(id);

            ImageView iconImageView = (ImageView)v.findViewById(android.R.id.icon);
            String school = party.getSchool();
            setImage(school, iconImageView);

            TextView titleTextView = (TextView)v.findViewById(android.R.id.title);
            titleTextView.setText(party.getTitle());

            TextView contentTextView = (TextView)v.findViewById(android.R.id.content);

            double distanceInMiles = mMyLocation.distanceTo(party.getLocation()) * 0.000621371;

            DecimalFormat df = new DecimalFormat("#.##");

            contentTextView.setText(df.format(distanceInMiles) + " miles away");

            // Returning the view containing InfoWindow contents
            return v;
        }

        private void setImage(String school, ImageView iconImageView) {
            if (school.equals("Northeastern"))
                iconImageView.setImageResource(R.drawable.logo_northeastern);
            else if (school.equals("MIT"))
                iconImageView.setImageResource(R.drawable.logo_mit);
            else if (school.equals("Boston University"))
                iconImageView.setImageResource(R.drawable.logo_bu);
            else if (school.equals("Harvard"))
                iconImageView.setImageResource(R.drawable.logo_harvard);
            else
                iconImageView.setImageResource(R.drawable.ic_launcher);
        }
    }

}
