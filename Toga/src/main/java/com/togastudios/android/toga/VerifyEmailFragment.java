package com.togastudios.android.toga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class VerifyEmailFragment extends Fragment {

    private static final String TAG = "VerifyEmailFragment";
    public static final String EXTRA_EMAIL = "com.togastudios.android.toga.verifyEmailFragment.email";

    private LinearLayout mContentLayout;
    private LinearLayout mProgressBarLayout;
    private Button mVerifyButton;

    private String mEmail;

    /**
     * Creates a new instance of the VerifyEmailFragment with arguments set.
     * @param email String containing the email address to verify
     * @return VerifyEmailFragment with arguments set
     */
    public static Fragment newInstance(String email) {
        Bundle args = new Bundle();
        args.putString(EXTRA_EMAIL, email);

        VerifyEmailFragment fragment = new VerifyEmailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * Sets mEmail field to the fragment argument.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Gets email address from argument
        mEmail = getArguments().getString(EXTRA_EMAIL);
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
        View v = inflater.inflate(R.layout.new_user_verify, container, false);
        assert v != null;

        // Obtain handles to UI objects
        mContentLayout = (LinearLayout) v.findViewById(R.id.verify_content);
        mProgressBarLayout = (LinearLayout) v.findViewById(R.id.verify_downloadProgress);
        mVerifyButton = (Button) v.findViewById(R.id.new_user_verify_button);

        // Register handler for UI elements
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        return v;
    }

    /**
     * Checks the user account on backend to determine if user's email has been verified yet.
     */
    private void verify() {
        // Sets the progress bar to spin until query is done
        mContentLayout.setVisibility(View.INVISIBLE);
        mProgressBarLayout.setVisibility(View.VISIBLE);

        // Queries for current user
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", mEmail);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                // Remove progress bar
                mContentLayout.setVisibility(View.VISIBLE);
                mProgressBarLayout.setVisibility(View.INVISIBLE);

                if (e == null) {
                    if (parseUser == null) {
                        // ERROR WITH REGISTRATION
                        Log.e(TAG, "Error with registration");
                        Toast.makeText(getActivity(), getResources().
                                getString(R.string.verify_email_registration_error), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (parseUser.getBoolean("emailVerified")) {
                            // If email has been verified
                            verifyConfirmed();
                        }
                        else {
                            // Email has not yet been verified
                            Toast.makeText(getActivity(), getResources().
                                    getString(R.string.verify_email_not_yet), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // The request failed
                    Toast.makeText(getActivity(), getResources().
                            getString(R.string.could_not_contact_servers), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Called when the email is verified. Finishes activity and launches main NavDrawerActivity.
     */
    public void verifyConfirmed() {
        Intent i = new Intent(getActivity(), NavDrawerActivity.class);
        startActivity(i);
        getActivity().finish();
    }

}
