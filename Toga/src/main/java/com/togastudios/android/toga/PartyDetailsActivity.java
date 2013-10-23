package com.togastudios.android.toga;

import android.support.v4.app.Fragment;

public class PartyDetailsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PartyDetailsFragment.newInstance(getIntent().getStringExtra(PartyDetailsFragment.EXTRA_PARTY));
    }
}
