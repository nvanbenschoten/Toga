package com.togastudios.android.toga;

import android.util.Log;

public class ThemePhotoBuilder {

    public static int getThemeResource(int themeId) {
        switch (themeId) {
            case 0:
                return R.drawable.black_light_party;
            case 1:
                return R.drawable.party_frat_house;
            default:
                Log.e("ThemePhotoBuilder", "Error: invalid theme id");
                return -1;
        }
    }

    public static int getSize() {
        return 2;
    }
}
