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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public abstract class NavDrawerFragmentActivity extends ActionBarActivity{
    private static final String PREFS_FIRST_RUN = "com.togasoftware.android.toga.firstRun";

    private String[] mDrawerChoices;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private SharedPreferences mPrefs;

    protected abstract Fragment getFragmentByPosition(int position);

    protected abstract int getFirstFrag();

    protected void selectNavDrawerItem(int position) {
        // Gets fragment selection from abstract class
        Fragment fragment = getFragmentByPosition(position);

        // If fragment is null
        if (fragment == null) {
            Toast.makeText(this, "Fragment not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Replaces current fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerChoices[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    protected int getLayoutResId() {
        // Returns the layout for the activity
        return R.layout.activity_fragment;
    }

    protected String[] getDrawerChoices() {
        // Gets the choices for the nav drawer
        return getResources().getStringArray(R.array.nav_drawer_array);
    }

    private void initializeNavDrawer() {
        // Gets class fields
        mDrawerChoices = getDrawerChoices();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Sets drawer listview dividers
        int[] colors = {0, 0xffcdcdcd, 0}; // red for the example
        mDrawerList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        mDrawerList.setDividerHeight(1);

        // Sets shadowing on drawer
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Sets drawer adaper
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerChoices));

        // Sets drawer click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Do stuff
                selectNavDrawerItem(position);
            }
        });

        // Sets action of drawer when clicked
        mTitle = mDrawerTitle = getTitle();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                ActivityCompat.invalidateOptionsMenu(NavDrawerFragmentActivity.this);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
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

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);



        // If first time opening app
        boolean firstRun = mPrefs.getBoolean(PREFS_FIRST_RUN, true);
        if (firstRun) {
            mPrefs.edit().putBoolean(PREFS_FIRST_RUN, false).commit();
            mDrawerLayout.openDrawer(Gravity.LEFT);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(<MENU_ITEMS>).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // After configuration change has occurred
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

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

    @Override
    public void setTitle(CharSequence title) {
        // Sets title on action bar
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /* Deals with the bug where no shadow appears under the action
       bar on android 4.3. Will be removed once bug is fixed.
     */
    private void setWindowContentOverlayCompat() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Get the content view
            View contentView = findViewById(android.R.id.content);

            // Make sure it's a valid instance of a FrameLayout
            if (contentView instanceof FrameLayout) {
                TypedValue tv = new TypedValue();

                // Get the windowContentOverlay value of the current theme
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

