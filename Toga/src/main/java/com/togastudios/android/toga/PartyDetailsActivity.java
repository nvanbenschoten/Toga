package com.togastudios.android.toga;

import android.support.v4.app.Fragment;

public class PartyDetailsActivity extends SingleFragmentActivity {

    /**
     * Extended method to determine which fragment to create in container of SingleFragmentActivity.
     * Creates new PartyDetailsFragment instance using string extra from intent.
     * @return Fragment which will be inflated in container
     */
    @Override
    protected Fragment createFragment() {
        return PartyDetailsFragment.newInstance(getIntent().
                getStringExtra(PartyDetailsFragment.EXTRA_PARTY));
    }

}
