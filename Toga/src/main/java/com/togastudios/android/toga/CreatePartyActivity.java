package com.togastudios.android.toga;

import android.support.v4.app.Fragment;

public class CreatePartyActivity extends SingleFragmentActivity {

    /**
     * Extended method to determine which fragment to create in container of SingleFragmentActivity.
     * Creates a new instance of CreatePartyFragment to return.
     * @return Fragment which will be inflated in container
     */
    @Override
    protected Fragment createFragment() {
        return new CreatePartyFragment();
    }

}
