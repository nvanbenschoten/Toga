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
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewUserPasswordFragment extends Fragment {

    private static final int max_password_length = 32;

    private LinearLayout mLinearLayout;
    private Button mContinueButton;
    private EditText mEmailEditText;

    private String mPassword;

    private passwordContinueButton mInterface;

    /**
     * Called when a fragment is first attached to its activity.
     * @param activity The activity the fragment is attached to
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (passwordContinueButton)activity;
    }

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * Also initializes the mPassword field.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Initialize mPassword to blank
        mPassword = "";
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
        View v = inflater.inflate(R.layout.new_user_password, container, false);
        assert v != null;

        // Obtain handles to UI objects
        mLinearLayout = (LinearLayout) v.findViewById(R.id.new_user_password_layout);
        mEmailEditText = (EditText) v.findViewById(R.id.new_user_password_edittext);
        mContinueButton = (Button) v.findViewById(R.id.new_user_password_continue);

        // Register handler for UI elements
        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPassword = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPassword(mPassword);
            }
        });

        return v;
    }

    /**
     * Verifies the user has typed in a password and that it's under 32 characters.
     * @param name String containing current username attempt
     */
    private void verifyPassword(String name) {
        if (name == null || name.length() == 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.new_user_enter_password),
                    Toast.LENGTH_LONG).show();
        }
        else if (name.length() > max_password_length) {
            Toast.makeText(getActivity(), getResources().getString(R.string.new_user_password_length_limit),
                    Toast.LENGTH_LONG).show();
        }
        else {
            mInterface.passwordButtonClicked(mPassword);
        }
    }

    /**
     * Interface which allows for communication from fragment to activity.
     */
    public interface passwordContinueButton {
        /**
         * Attempts to set the password of the new user.
         * @param password String containing the password attempt
         */
        public void passwordButtonClicked(String password);
    }
}
