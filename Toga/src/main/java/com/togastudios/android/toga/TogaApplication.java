package com.togastudios.android.toga;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class TogaApplication extends Application {

    /**
     * Called when Application is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Calls init helper function
        initializeParse();
    }

    /**
     * Initialize the Parse backend and register Party subclass.
     */
    private void initializeParse() {
        // Registering subclasses
        ParseObject.registerSubclass(Party.class);

        // Initializing Parse keys
        Parse.initialize(this, "xDb3CZ26dRoCOlbcR5IHz6I1mFzkHGm57xnkszgT", "l41Q8ZWiIPGu7ALAKjaoJQtlosGqi8UeHmDMh1Ae");
    }

}
