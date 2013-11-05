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

    private TextView mNameTextView;
    private ImageView mIconImageView;

    private ParseUser mProfile;

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * Creates a new party and initializes its fields.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        // Acquire handle to current user
        mProfile = ParseUser.getCurrentUser();
        if (mProfile == null)
            getActivity().finish();
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
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        assert v != null;

        // Obtain handles to UI objects
        mNameTextView = (TextView) v.findViewById(android.R.id.title);
        mIconImageView = (ImageView) v.findViewById(android.R.id.icon);
        TextView contentTextView = (TextView) v.findViewById(android.R.id.content);

        // Set UI element data
        mNameTextView.setText(mProfile.getUsername());
        PhotoBuilder.setSchoolImage(mIconImageView, mProfile.getString("school"));
        contentTextView.setText(mProfile.getEmail());

        return v;
    }

    /**
     * Inflate menu for the fragment once the options menu has been created.
     * @param menu The options menu in which items are placed
     * @param inflater The MenuInflater object that can be used to inflate menu items in the menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_fragment, menu);
    }

    /**
     * Handle menu item selection interactions.
     * @param item The menu item that was selected
     * @return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // Log the user out and close app
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(), NewUserActivity.class);
                startActivity(intent);
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
