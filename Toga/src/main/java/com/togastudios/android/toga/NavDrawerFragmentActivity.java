package com.togastudios.android.toga;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public abstract class NavDrawerFragmentActivity extends ActionBarActivity{

    private static final String TAG = "NavDrawerFragmentActivity";
    private static final String PREFS_FIRST_RUN = "com.togasoftware.android.toga.firstRun";

    private static final int drawer_divider_color = 0xffcdcdcd;

    private String[] mDrawerChoices;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private SharedPreferences mPrefs;

    /**
     * Determines which fragments correspond to which positions in the nav drawer.
     * @param position The position on the nav drawer
     * @return The fragment which should be inflated for the input position
     */
    protected abstract Fragment getFragmentByPosition(int position);

    /**
     * Determines the fragment position which should be set at application launch.
     * @return App launch nav drawer default position
     */
    protected abstract int getFirstFrag();

    /**
     * Inflates a fragment into the fragment container based on the navigation drawer position
     * clicked. Also sets title and nav drawer item to clicked.
     * @param position The position on the navigation drawer
     */
    protected void selectNavDrawerItem(int position) {
        // Gets fragment selection from abstract class
        Fragment fragment = getFragmentByPosition(position);

        // If fragment is null
        if (fragment == null) {
            Log.e(TAG, "Fragment not found");
            return;
        }

        // Replaces current fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        // update selected item and title, then close the drawer
        setTitle(mDrawerChoices[position]);
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * Determines the layout for the activity to use. Can be extended and overridden by subclasses.
     * @return The layout resource id which will be inflated
     */
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    /**
     * Gets the string choices for the navigation drawer adapter from resources
     * @return String array containing the nav drawer choices
     */
    protected String[] getDrawerChoices() {
        return getResources().getStringArray(R.array.nav_drawer_array);
    }

    /**
     * Called when the activity is first created. Responsible for initializing the activity.
     * @param savedInstanceState Bundle state the activity is saved in (null on clean start)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        setWindowContentOverlayCompat();

        // Initializes navigation drawer
        initializeNavDrawer();

        // Sets nav drawer if new activity
        if (savedInstanceState == null) {
            selectNavDrawerItem(getFirstFrag());
        }

        // Get preference manager
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // If first time opening app, open drawer
        boolean firstRun = mPrefs.getBoolean(PREFS_FIRST_RUN, true);
        if (firstRun) {
            mPrefs.edit().putBoolean(PREFS_FIRST_RUN, false).commit();
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    /**
     * Inflate menu for the activity once the options menu has been created.
     * @param menu  The options menu in which items are placed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Prepare the options menu to be displayed. Called right before the menu is shown,
     * every time it is shown. Use to efficiently enable/disable items dynamically.
     * @param menu The options menu as last shown or first initialized by onCreateOptionsMenu
     * @return Return true for the menu to be displayed, false it will not be shown
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(<MENU_ITEMS>).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Called when activity start-up is complete.
     * @param savedInstanceState Bundle state the activity is saved in (null on clean start)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * Called by the system when the device configuration changes.
     * @param newConfig The new device configuration.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // After configuration change has occurred
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Handle menu item selection interactions.
     * @param item  The menu item that was selected
     * @return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes the navigation drawer and sets action handlers.
     */
    private void initializeNavDrawer() {
        // Obtain handles to drawer UI objects
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Get choices
        mDrawerChoices = getDrawerChoices();

        // Sets drawer list dividers
        int[] colors = {0, drawer_divider_color, 0}; // red for the example
        mDrawerList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        mDrawerList.setDividerHeight(1);

        // Sets shadowing on drawer
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Sets drawer adapter
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerChoices));

        // Sets drawer click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectNavDrawerItem(position);
            }
        });

        // Sets action of drawer when clicked
        mTitle = mDrawerTitle = getTitle();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                // Set title to app title and invalidate
                getSupportActionBar().setTitle(mDrawerTitle);
                ActivityCompat.invalidateOptionsMenu(NavDrawerFragmentActivity.this);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Set title to fragment title and invalidate
                getSupportActionBar().setTitle(mTitle);
                ActivityCompat.invalidateOptionsMenu(NavDrawerFragmentActivity.this);
            }
        };

        // Sets listener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Sets up action bar to support nav drawer
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    /**
     * Sets the action bar title and sets title field to keep track of title.
     * @param title CharSequence containing new title
     */
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * Deals with the bug where no shadow appears under the action bar on android 4.3.
     * Will be removed once bug is fixed.
     */
    private void setWindowContentOverlayCompat() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Get the content view
            View contentView = findViewById(android.R.id.content);

            // Make sure it's a valid instance of a FrameLayout
            if (contentView instanceof FrameLayout) {
                TypedValue tv = new TypedValue();

                // Get the windowContentOverlay value of the current theme
                assert  getTheme() != null;
                if (getTheme().resolveAttribute(
                        android.R.attr.windowContentOverlay, tv, true)) {

                    // If it's a valid resource, set it as the foreground drawable
                    // for the content view
                    if (tv.resourceId != 0) {
                        ((FrameLayout) contentView).setForeground(
                                getResources().getDrawable(tv.resourceId));
                    }
                }
            }
        }
    }

}

