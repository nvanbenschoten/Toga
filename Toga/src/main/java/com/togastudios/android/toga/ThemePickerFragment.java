package com.togastudios.android.toga;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class ThemePickerFragment extends ListFragment {

    public static final String EXTRA_THEME =
            "com.togastudios.android.toga.theme_picker";

    ArrayList<FrameLayout> mImageViews;

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * Creates an array of theme drawable resources.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        // Set up button
        ((ThemePickerActivity)getActivity()).getSupportActionBar().
                setDisplayHomeAsUpEnabled(true);

        // Create array of resource integers
        ArrayList<Integer> themes = new ArrayList<Integer>();
        for (int i = 0; i < PhotoBuilder.getThemeSize(); i++) {
            themes.add(PhotoBuilder.getThemeResource(i));
        }

        // Initializes ArrayList of FrameLayouts for clicking
        mImageViews = new ArrayList<FrameLayout>();

        // Set list adapter
        setListAdapter(new ThemePhotoAdapter(themes));
    }

    /**
     * Called after the fragment view first created. Responsible for initializing data based
     * on the UI.
     * @param view The View returned by onCreateView
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Removes ListView dividers
        getListView().setDividerHeight(0);
    }

    /**
     * Handle menu item selection interactions.
     * @param item The menu item that was selected
     * @return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Up button
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when theme is clicked.
     * @param l The ListView where the click happened
     * @param v The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id The row id of the item that was clicked
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        returnActivity(position);
    }

    /**
     * Finishes activity with theme id set as result and result code set to OK.
     * @param position int position of the theme clicked (corresponds to theme id)
     */
    private void returnActivity(int position) {
        Intent i = new Intent();
        i.putExtra(EXTRA_THEME, position);
        getActivity().setResult(Activity.RESULT_OK, i);
        getActivity().finish();
    }

    /**
     * Theme adapter which holds each party theme.
     */
    private class ThemePhotoAdapter extends ArrayAdapter<Integer> {

        /**
         * Public constructor allows for ThemePhotoAdapter instantiation.
         * @param themes ArrayList of theme resource id's
         */
        public ThemePhotoAdapter(ArrayList<Integer> themes) {
            super(getActivity(), 0, themes);
        }

        /**
         * Get a View that displays the data at the specified position in the data set.
         * @param position integer position of view in array
         * @param convertView Old view to reuse, if possible. If not, is null
         * @param parent The parent that this view will eventually be attached to
         * @return View corresponding to the data at the specified position
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Not given a view
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.theme_list_item, null);
            }
            assert convertView != null;

            // Configure view for this resource
            int res = getItem(position);

            // Obtain handles to UI objects
            FrameLayout frames = (FrameLayout) convertView.findViewById(R.id.theme_list_layout);
            ImageView themeImage = (ImageView) convertView.findViewById(R.id.theme_list_photo);
            FrameLayout background = (FrameLayout) convertView.findViewById(R.id.theme_list_layout_background);

            // Set data for UI elements
            themeImage.setImageResource(res);
            background.setTag(position);
            mImageViews.add(position, background);

            // Register handler for UI elements
            mImageViews.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer realPosition = (Integer) v.getTag();
                    returnActivity(realPosition);
                }
            });

            return convertView;
        }

    }

}
