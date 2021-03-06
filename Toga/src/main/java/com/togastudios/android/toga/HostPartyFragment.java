package com.togastudios.android.toga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HostPartyFragment extends Fragment {

    private static final String TAG = "HostPartyFragment";

    private LinearLayout mProgressBar;
    private FrameLayout mContent;
    private GridView mGridView;
    private LinearLayout mEmptyLayout;
    private Button mEmptyButton;

    /**
     * Called when the fragment is first created. Responsible for initializing the fragment.
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    /**
     * Called when the fragment is first created. Responsible for initializing the UI.
     * @param inflater  The LayoutInflater object that can be used to inflate views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle state the fragment is saved in (null on clean start)
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_host_party_list, container, false);
        assert v != null;

        // Obtain handles to UI objects
        mGridView = (GridView) v.findViewById(android.R.id.list);
        mEmptyButton = (Button) v.findViewById(R.id.empty_host_party);
        mProgressBar = (LinearLayout) v.findViewById(R.id.host_list_downloadProgress);
        mContent = (FrameLayout) v.findViewById(R.id.host_list_content);
        mEmptyLayout = (LinearLayout) v.findViewById(android.R.id.empty);

        // Register handler for UI elements
        mEmptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostPartyButtonClicked();
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                partyClicked(((Party)mGridView.getAdapter().getItem(position)).getId());
            }
        });

        return v;
    }

    /**
     * Called each time the fragment is started. Responsible for initializing data.
     */
    @Override
    public void onStart() {
        super.onResume();
        mGridView.setAdapter(null);
        getHostedParties();
    }

    /**
     * Inflate menu for the fragment once the options menu has been created.
     * @param menu      The options menu in which items are placed
     * @param inflater  The MenuInflater object that can be used to inflate menu items in the menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.host_party_fragment, menu);
    }

    /**
     * Handle menu item selection interactions.
     * @param item  The menu item that was selected
     * @return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_host_party:
                // Creates a new party
                hostPartyButtonClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launches the PartyDetailsActivity to show the selected parties details.
     * @param id party id whose details are to be shown in detail acitivity
     */
    private void partyClicked(String id) {
        Intent i = new Intent(getActivity(), PartyDetailsActivity.class);
        i.putExtra(PartyDetailsFragment.EXTRA_PARTY, id);
        startActivity(i);
    }

    /**
     * Launches the CreatePartyActivity to allow for party creation.
     */
    private void hostPartyButtonClicked() {
        Intent i = new Intent(getActivity(), CreatePartyActivity.class);
        startActivity(i);
    }

    /**
     * Populate the party list based on all parties hosted by the user.
     */
    private void getHostedParties() {
        // Sets the progress bar to spin until query is done
        mProgressBar.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);

        // Get the parties the host is hosting
        ParseQuery<Party> query = ParseQuery.getQuery(Party.class);
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.whereEqualTo(Party.hostKey, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Party>() {
            public void done(List<Party> partyList, ParseException e) {
                if (getActivity() == null) return;

                // Remove progress bar
                mProgressBar.setVisibility(View.GONE);
                mContent.setVisibility(View.VISIBLE);

                if (e == null) {
                    if (partyList.isEmpty()) {
                        // If list is empty
                        mGridView.setVisibility(View.INVISIBLE);
                        mEmptyLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        // If list is not empty
                        mGridView.setVisibility(View.VISIBLE);
                        mEmptyLayout.setVisibility(View.INVISIBLE);
                        ArrayList<Party> partyArrayList = new ArrayList<Party>(partyList);

                        PartyCardAdapter adapter = new PartyCardAdapter(getActivity(), partyArrayList);
                        mGridView.setAdapter(adapter);
                    }
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

}
