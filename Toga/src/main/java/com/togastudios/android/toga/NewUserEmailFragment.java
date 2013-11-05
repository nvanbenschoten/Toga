package com.togastudios.android.toga;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class NewUserEmailFragment extends Fragment {

    private static final int parse_no_query_match = 101;

    private LinearLayout mLinearLayout;
    private ImageView mImageView;
    private EditText mEmailEditText;
    private Button mContinueButton;

    private String mEmail;

    private emailContinueButton mInterface;

    /**
     * Called when a fragment is first attached to its activity.
     * @param activity The activity the fragment is attached to
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (emailContinueButton)activity;
    }

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * Also initializes the mEmail field.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Initialize mEmail to blank
        mEmail = "";
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
        View v = inflater.inflate(R.layout.new_user_email, container, false);
        assert v != null;

        // Obtain handles to UI objects
        mLinearLayout = (LinearLayout) v.findViewById(R.id.new_user_email_layout);
        mImageView = (ImageView) v.findViewById(R.id.new_user_email_picture);
        mEmailEditText = (EditText) v.findViewById(R.id.new_user_email_edittext);
        mContinueButton = (Button) v.findViewById(R.id.new_user_email_continue);

        // Register handler for UI elements
        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmail = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmail(mEmail);
            }
        });

        return v;
    }

    /**
     * Checks the email string to assure validity of the entry.
     * If a duplicate email, login. If a new email, sign up.
     * @param email String with email address contained
     */
    private void checkEmail(String email) {
        // Parse query to see if email already registered
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null || e.getCode() == parse_no_query_match) {
                    if (parseUser != null) {
                        // Matches stored email
                        mInterface.emailButtonClickedLogin(parseUser.getUsername());
                    }
                    else {
                        // No matches, makes sure email is valid
                        int ampIndex = mEmail.indexOf('@');
                        if (ampIndex == -1) {
                            emailError();
                            return;
                        }

                        String domain = mEmail.substring(ampIndex + 1);

                        if (((NewUserActivity)getActivity()).getEmailDomainsMap().containsKey(domain)) {
                            mInterface.emailButtonClickedNew(mEmail);
                        }
                        else {
                            emailError();
                        }
                    }
                } else {
                    // The request failed
                    Toast.makeText(getActivity(), getResources().getString(R.string.could_not_contact_servers),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Prints a warning stating that email address is invalid.
     */
    private void emailError() {
        Toast.makeText(getActivity(), getResources().getString(R.string.new_user_valid_email),
                Toast.LENGTH_LONG).show();
    }

    /**
     * Interface which allows for communication from fragment to activity.
     */
    public interface emailContinueButton {
        /**
         * Invalid (new) email entered, need to sign up.
         * @param email String containing new email address
         */
        public void emailButtonClickedNew(String email);

        /**
         * Valid (already used) email entered, need to log in.
         * @param username String containing valid email address
         */
        public void emailButtonClickedLogin(String username);
    }

}
