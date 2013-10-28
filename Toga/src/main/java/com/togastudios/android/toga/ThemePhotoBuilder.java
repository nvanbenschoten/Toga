package com.togastudios.android.toga;

import android.util.Log;

public class ThemePhotoBuilder {

    public static int getThemeResource(int themeId) {
        switch (themeId) {
            case 0:
                return R.drawable.theme_black_light_party;
            case 1:
                return R.drawable.theme_party_frat_house;
            case 2:
                return R.drawable.theme_sports_bar;
            case 3:
                return R.drawable.theme_halloween;
            default:
                Log.e("ThemePhotoBuilder", "Error: invalid theme id");
                return -1;
        }
    }

    public static int getSize() {
        return 4;
    }
}
