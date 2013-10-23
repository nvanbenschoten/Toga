package com.togastudios.android.toga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private ParseUser mProfile;
    private TextView mNameTextView;
    private ImageView mIconImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mProfile = ParseUser.getCurrentUser();
        if (mProfile == null)
            getActivity().finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mNameTextView = (TextView)v.findViewById(android.R.id.title);
        mNameTextView.setText(mProfile.getUsername());

        mIconImageView = (ImageView)v.findViewById(android.R.id.icon);
        String school = mProfile.getString("school");
        setImage(school);

        TextView contentTextView = (TextView)v.findViewById(android.R.id.content);
        contentTextView.setText(mProfile.getEmail());

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(), NewUserActiviy.class);
                startActivity(intent);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
}
