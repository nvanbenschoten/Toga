package com.togastudios.android.toga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

public class NavDrawerActivity extends NavDrawerFragmentActivity {

    /**
     * Determines which fragments correspond to which positions in the nav drawer.
     * @param position The position on the nav drawer
     * @return The fragment which should be inflated for the input position
     */
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

    /**
     * Determines the fragment position which should be set at application launch
     * @return App launch nav drawer default position
     */
    @Override
    protected int getFirstFrag() {
        return 1;
    }

    /**
     * Called when the activity is first created. Responsible for initializing the activity.
     * Signs user in and verifies email address.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signIn();
        verifyEmail();
    }

    /**
     * Determines if the user is signed in, and if not, launches NewUserActivity.
     */
    private void signIn() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            // If no current user
            Intent i = new Intent(this, NewUserActivity.class);
            startActivity(i);
            finish();
        }
    }

    /**
     * Verifies that the current user's email is verified.
     */
    private void verifyEmail() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        // If no current user, re-sign in
        if (currentUser == null) {
            signIn();
            return;
        }

        // Checks if verified
        if (currentUser.getBoolean("emailVerified"))
            return;

        // If current is not verified, tries to fetch a new current user version
        try {
            currentUser.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Re-checks if the user is verified
        if (currentUser.getBoolean("emailVerified"))
            return;

        // If not, try to verify
        String mEmail = currentUser.getEmail();

        if (mEmail == null) {
            Intent i = new Intent(this, NewUserActivity.class);
            startActivity(i);
            finish();
            return;
        }

        int ampIndex = mEmail.indexOf('@');
        assert ampIndex != -1;

        String domain = mEmail.substring(ampIndex + 1);

        // Makes sure email is not a test email
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
