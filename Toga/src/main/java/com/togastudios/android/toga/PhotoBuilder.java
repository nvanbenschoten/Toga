package com.togastudios.android.toga;

import android.util.Log;
import android.widget.ImageView;

public class PhotoBuilder {

    private static final String TAG = "PhotoBuilder";

    /**
     * Gets the resource identifier of the drawable corresponding to the given theme.
     * @param themeId int containing the themeId
     * @return resource identifier with theme drawable
     */
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
                Log.e(TAG, "Error: invalid theme id");
                return -1;
        }
    }

    /**
     * Gets the number of themes currently supported.
     * @return int containing number of themes
     */
    public static int getThemeSize() {
        return 4;
    }

    /**
     * Sets the school icon corresponding to the party's school.
     * @param imageView ImageView which is to be given an image resource
     * @param school String containing the party's school
     */
    public static void setSchoolImage(ImageView imageView, String school) {
        if (school.equals("Northeastern"))
            imageView.setImageResource(R.drawable.logo_northeastern);
        else if (school.equals("MIT"))
            imageView.setImageResource(R.drawable.logo_mit);
        else if (school.equals("Boston University"))
            imageView.setImageResource(R.drawable.logo_bu);
        else if (school.equals("Harvard"))
            imageView.setImageResource(R.drawable.logo_harvard);
        else {
            imageView.setImageResource(R.drawable.ic_launcher);
            Log.e(TAG, "Error: invalid school");
        }
    }

}
