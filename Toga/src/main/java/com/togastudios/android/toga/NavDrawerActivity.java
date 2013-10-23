package com.togastudios.android.toga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

public class NavDrawerActivity extends NavDrawerFragmentActivity {

    public static final String PREFS_USERNAME = "com.togasoftware.android.toga.username";
    public static final String PREFS_PASSWORD = "com.togasoftware.android.toga.password";

    private SharedPreferences mPrefs;

    @Override
    protected Fragment getFragmentByPosition(int position) {
        switch (position) {
            case 0:
                return PartyDetailsFragment.newInstance(PartyDetailsFragment.EXTRA_NOW);
            case 1:
                return new BrowsePartyFragment();
            case 2:
                return new PartyMapFragment();
            case 3:
                return new ProfileFragment();
            case 4:
                return new HostPartyFragment();
            case 5:
                return new InvitesFragment();
            default:
                return new BrowsePartyFragment();
        }
    }

    @Override
    protected int getFirstFrag() {
        // Returns the frag that should start
        return 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gets hold of shared prefs
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        signIn();
        verifyEmail();
    }

    private void signIn() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            // If no current user

            Intent i = new Intent(this, NewUserActiviy.class);
            startActivity(i);
            finish();
        }
    }

    private void verifyEmail() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            signIn();
            return;
        }

        if (currentUser.getBoolean("emailVerified"))
            return;

        // If current is not verified, tries to fetch a new current user version
        try {
            currentUser.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // If verified no, good
        if (currentUser.getBoolean("emailVerified"))
            return;

        // If not, try to verify
        String mEmail = currentUser.getEmail();

        if (mEmail == null) {
            Intent i = new Intent(this, NewUserActiviy.class);
            startActivity(i);
            finish();
            return;
        }
        int ampIndex = mEmail.indexOf('@');
        if (ampIndex == -1)
            return;

        String domain = mEmail.substring(ampIndex + 1);

        if (!domain.equals("test.edu")) {
            Intent i = new Intent(this, VerifyEmailActivity.class);
            i.putExtra(VerifyEmailFragment.EXTRA_EMAIL, mEmail);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "You are using a test email, no need to verify!", Toast.LENGTH_SHORT).show();
        }

    }
}
