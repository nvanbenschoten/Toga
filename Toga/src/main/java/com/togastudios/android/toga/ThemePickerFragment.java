package com.togastudios.android.toga;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create array of resource integers
        ArrayList<Integer> themes = new ArrayList<Integer>();
        for (int i = 0; i < ThemePhotoBuilder.getSize(); i++) {
            themes.add(ThemePhotoBuilder.getThemeResource(i));
        }

        mImageViews = new ArrayList<FrameLayout>();

        setListAdapter(new ThemePhotoAdapter(themes));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDividerHeight(0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        returnActivity(position);
    }

    private void returnActivity(int position) {
        // Creates intent with extras
        Intent i = new Intent();
        i.putExtra(EXTRA_THEME, position);
        getActivity().setResult(Activity.RESULT_OK, i);
        getActivity().finish();
    }

    private class ThemePhotoAdapter extends ArrayAdapter<Integer> {

        public ThemePhotoAdapter(ArrayList<Integer> themes) {
            super(getActivity(), 0, themes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Not given a view
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.theme_list_item, null);
            }

            // Configure view for this resource
            int res = getItem(position);

            FrameLayout frames = (FrameLayout)convertView.findViewById(R.id.theme_list_layout);

            ImageView themeImage = (ImageView)convertView.findViewById(R.id.theme_list_photo);
            themeImage.setImageResource(res);

            FrameLayout background = (FrameLayout)convertView.findViewById(R.id.theme_list_layout_background);
            background.setTag(new Integer(position));
            mImageViews.add(position, background);

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
