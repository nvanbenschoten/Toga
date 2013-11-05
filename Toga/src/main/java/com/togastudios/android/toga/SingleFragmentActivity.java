package com.togastudios.android.toga;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

public abstract class SingleFragmentActivity extends ActionBarActivity{

    /**
     * Determines which fragments should be inflated in the container.
     * @return The fragment which should be inflated
     */
    protected abstract Fragment createFragment();

    /**
     * Determines the layout for the activity to use. Can be extended and overridden by subclasses.
     * @return The layout resource id which will be inflated
     */
    protected int getLayoutResId() {
        return R.layout.activity_fragment_no_drawer;
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

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
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

