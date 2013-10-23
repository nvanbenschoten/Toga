package com.togastudios.android.toga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;

public class NewUserActiviy extends SingleFragmentActivity
        implements  NewUserEmailFragment.emailContinueButton,
                    NewUserNameFragment.nameContinueButton,
                    NewUserPasswordFragment.passwordContinueButton,
                    NewUserLoginFragment.loginContinueButton {

    private ParseUser mNewStudent;
    private FragmentManager mFragmentManager;

    private String mEmail;
    private String mUsername;
    private HashMap<String, String> mEmailDomainsMap;
    private String [] emailDomains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        mFragmentManager = getSupportFragmentManager();

        mNewStudent = new ParseUser();

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
    protected Fragment createFragment() {
        return new NewUserEmailFragment();
    }

    @Override
    public void emailButtonClickedNew(String email) {
        // Register a new user
        mEmail = email;
        mNewStudent.setEmail(email);

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

    @Override
    public void emailButtonClickedLogin(String username) {
        mUsername = username;

        // Log in
        // Ask for password
        // New fragment to get more info
        Fragment fragment = new NewUserLoginFragment();
        mFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter, R.anim.exit)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void nameButtonClicked(String name) {
        mNewStudent.setUsername(name);

        // New fragment to get more info
        Fragment fragment = new NewUserPasswordFragment();
        mFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.enter, R.anim.exit)
                .replace(R.id.fragmentContainer, fragment)
                .commit();

    }

    @Override
    public void passwordButtonClicked(String password) {
        // Create account
        mNewStudent.setPassword(password);

        mNewStudent.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(NewUserActiviy.this, "Signed up!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(NewUserActiviy.this, NavDrawerActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(NewUserActiviy.this, "Failed to sign up", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void loginButtonClicked(String password) {
        ParseUser.logInInBackground(mUsername, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(NewUserActiviy.this, NavDrawerActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
