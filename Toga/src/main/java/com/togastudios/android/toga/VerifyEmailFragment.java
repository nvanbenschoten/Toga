package com.togastudios.android.toga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    public static final String EXTRA_EMAIL = "com.togastudios.android.toga.newuser.extraemail";

    private LinearLayout mContentLayout;
    private LinearLayout mProgressBarLayout;

    private String mEmail;

    private Button mVerifyButton;

    public static Fragment newInstance(String email) {
        Bundle args = new Bundle();
        args.putString(EXTRA_EMAIL, email);

        VerifyEmailFragment fragment = new VerifyEmailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mEmail = getArguments().getString(EXTRA_EMAIL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_user_verify, container, false);

        mContentLayout = (LinearLayout)v.findViewById(R.id.verify_content);

        mProgressBarLayout = (LinearLayout)v.findViewById(R.id.verify_downloadProgress);

        mVerifyButton = (Button)v.findViewById(R.id.new_user_verify_button);
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        return v;
    }

    private void verify() {
        mContentLayout.setVisibility(View.INVISIBLE);
        mProgressBarLayout.setVisibility(View.VISIBLE);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", mEmail);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    if (parseUser == null) {
                        //ERROR WITH REGISTRATION
                        Toast.makeText(getActivity(), "There was an error with the registration", Toast.LENGTH_SHORT).show();
                        mContentLayout.setVisibility(View.VISIBLE);
                        mProgressBarLayout.setVisibility(View.INVISIBLE);
                    }
                    else {
                        if (parseUser.getBoolean("emailVerified")) {
                            // If email has been verified
                            verifyConfirmed();
                        }
                        else {
                            // Email has not yet been verified
                            Toast.makeText(getActivity(), "The confirmation email has not yet been verified", Toast.LENGTH_SHORT).show();
                            mContentLayout.setVisibility(View.VISIBLE);
                            mProgressBarLayout.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    // The request failed
                    Toast.makeText(getActivity(), "Could not contact email servers", Toast.LENGTH_SHORT).show();
                    mContentLayout.setVisibility(View.VISIBLE);
                    mProgressBarLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void verifyConfirmed() {
        Intent i = new Intent(getActivity(), NavDrawerActivity.class);
        startActivity(i);
        getActivity().finish();
    }

}
