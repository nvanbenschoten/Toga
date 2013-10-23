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

import java.util.HashMap;

public class NewUserEmailFragment extends Fragment {

    private emailContinueButton mInterface;
    private LinearLayout mLinearLayout;
    private Button mContinueButton;
    private EditText mEmailEditText;
    private String mEmail;
    private ImageView mImageView;

    private HashMap<String, String> mEmailDomainsMap;
    private String [] emailDomains;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mEmail = "";

        // Creates hashmap
        emailDomains = getResources().getStringArray(R.array.email_map);
        mEmailDomainsMap = new HashMap<String, String>();

        for (int i = 0; i < emailDomains.length; i++) {
            int divisionIndex = emailDomains[i].indexOf('|');

            String school = emailDomains[i];

            mEmailDomainsMap.put(school.substring(0, divisionIndex),
                    school.substring(divisionIndex + 1));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (emailContinueButton)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_user_email, container, false);

        mLinearLayout = (LinearLayout)v.findViewById(R.id.new_user_email_layout);

        mImageView = (ImageView)v.findViewById(R.id.new_user_email_picture);

        mEmailEditText = (EditText)v.findViewById(R.id.new_user_email_edittext);
        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmail = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mContinueButton = (Button)v.findViewById(R.id.new_user_email_continue);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmail(mEmail);
            }
        });

        return v;
    }

    private void checkEmail(String email) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null || e.getCode() == 101) {
                    if (parseUser != null) {
                        // Matches stored email
                        mInterface.emailButtonClickedLogin(parseUser.getUsername());
                    }
                    else {
                        // No matches
                        int ampIndex = mEmail.indexOf('@');
                        if (ampIndex == -1) {
                            emailError();
                            return;
                        }

                        String domain = mEmail.substring(ampIndex + 1);

                        if (mEmailDomainsMap.containsKey(domain)) {
                            mInterface.emailButtonClickedNew(mEmail);
                        }
                        else {
                            emailError();
                        }
                    }
                } else {
                    // The request failed
                    Toast.makeText(getActivity(), "Could not contact email servers", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void emailError() {
        Toast.makeText(getActivity(), "Please enter a valid college email account.", Toast.LENGTH_LONG).show();
    }

    public interface emailContinueButton {
        public void emailButtonClickedNew(String email);
        public void emailButtonClickedLogin(String username);
    }
}
