package com.togastudios.android.toga;

import android.support.v4.app.Fragment;

public class ThemePickerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ThemePickerFragment();
    }
}
