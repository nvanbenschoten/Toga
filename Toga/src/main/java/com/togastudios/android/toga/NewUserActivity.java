package com.togastudios.android.toga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;

public class NewUserActivity extends SingleFragmentActivity
        implements  NewUserEmailFragment.emailContinueButton,
                    NewUserNameFragment.nameContinueButton,
                    NewUserPasswordFragment.passwordContinueButton,
                    NewUserLoginFragment.loginContinueButton {

    private static final String TAG = "NewUserActivity";

    private ParseUser mNewStudent;
    private FragmentManager mFragmentManager;

    private HashMap<String, String> mEmailDomainsMap;
    private String [] emailDomains;

    /**
     * Extended method to determine which fragment to create in container of SingleFragmentActivity.
     * Creates a new instance of NewUserEmailFragment to return.
     * @return Fragment which will be inflated in container
     */
    @Override
    protected Fragment createFragment() {
        return new NewUserEmailFragment();
    }

    /**
     * Called when the activity is first created. Responsible for initializing the activity.
     * @param savedInstanceState Bundle state the activity is saved in (null on clean start)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        // Obtain handles to fragment manager and parse user
        mFragmentManager = getSupportFragmentManager();
        mNewStudent = new ParseUser();

        // Creates HashMap to store school data
        emailDomains = getResources().getStringArray(R.array.email_map);
        mEmailDomainsMap = new HashMap<String, String>();
        for (String emailDomain : emailDomains) {
            // Divides on '|' and stores in HashMap
            int divisionIndex = emailDomain.indexOf('|');

            mEmailDomainsMap.put(emailDomain.substring(0, divisionIndex),
                    emailDomain.substring(divisionIndex + 1));
        }
    }

    /**
     * Sets the new user's email to the parameter and transitions to NewUserNameFragment.
     * @param email String containing new email address
     */
    @Override
    public void emailButtonClickedNew(String email) {
        // Register a new user with email address
        mNewStudent.setEmail(email);

        // Sets new users school
        int ampIndex = email.indexOf('@');
        String domain = email.substring(ampIndex + 1);
        mNewStudent.put("school", mEmailDomainsMap.get(domain));

        // New fragment to get more info
        Fragment fragment = new NewUserNameFragment();
        mFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter, R.anim.exit)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    /**
     * Sets the new user's username to the parameter and transitions to NewUserPasswordFragment.
     * @param name String containing the name attempt
     */
    @Override
    public void nameButtonClicked(String name) {
        // Sets new user's username
        mNewStudent.setUsername(name);

        // New fragment to get more info
        Fragment fragment = new NewUserPasswordFragment();
        mFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter, R.anim.exit)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    /**
     * Sets the new user's password to the parameter and attempts to sign up in background.
     * @param password String containing the password attempt
     */
    @Override
    public void passwordButtonClicked(String password) {
        // Sets new user's password
        mNewStudent.setPassword(password);

        // Signs up in background
        mNewStudent.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(NewUserActivity.this, getString(R.string.new_user_signed_up),
                            Toast.LENGTH_LONG).show();
                    Intent i = new Intent(NewUserActivity.this, NavDrawerActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Log.d(TAG, "Failed to sign up");
                    Toast.makeText(NewUserActivity.this, getString(R.string.new_user_failed_to_sign_up),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Sets the valid user's username to the parameter and transitions to NewUserLoginFragment.
     * @param username String containing valid username
     */
    @Override
    public void emailButtonClickedLogin(String username) {
        // Set new user's username
        mNewStudent.setUsername(username);

        // Ask for password so that user can log in
        // New fragment to get more info
        Fragment fragment = new NewUserLoginFragment();
        mFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter, R.anim.exit)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    /**
     * Attempt to log in using the users username and password.
     * @param password String containing the current password attempt
     */
    @Override
    public void loginButtonClicked(String password) {
        assert getApplicationContext() != null;

        ParseUser.logInInBackground(mNewStudent.getUsername(), password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.new_user_logged_in),
                            Toast.LENGTH_LONG).show();
                    Intent i = new Intent(NewUserActivity.this, NavDrawerActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Log.d(TAG, "Failed to log in");
                    Toast.makeText(getApplicationContext(), "Failed to login",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Gets the email domain map which maps Domain -> School.
     * @return HashMap with String key and String value
     */
    public HashMap<String, String> getEmailDomainsMap() {
        return mEmailDomainsMap;
    }

}
