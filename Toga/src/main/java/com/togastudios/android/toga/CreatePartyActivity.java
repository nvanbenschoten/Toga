package com.togastudios.android.toga;

import android.support.v4.app.Fragment;

public class CreatePartyActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CreatePartyFragment();
    }

}
