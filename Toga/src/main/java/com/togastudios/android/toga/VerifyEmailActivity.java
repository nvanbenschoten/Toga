package com.togastudios.android.toga;

import android.support.v4.app.Fragment;

public class VerifyEmailActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return VerifyEmailFragment.newInstance(getIntent().getStringExtra(VerifyEmailFragment.EXTRA_EMAIL));
    }
}
