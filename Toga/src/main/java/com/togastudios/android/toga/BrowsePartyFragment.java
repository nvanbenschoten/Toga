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

import java.util.ArrayList;
import java.util.List;

public class BrowsePartyFragment extends Fragment {
    private static final String TAG = "BrowsePartyFragment";

    private GridView mGridView;
    private Button mEmptyButton;
    private LinearLayout mProgressBar;
    private FrameLayout mContent;
    private LinearLayout mEmptyLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_browse_party_list, container, false);

        mEmptyLayout = (LinearLayout)v.findViewById(android.R.id.empty);
        mEmptyLayout.setVisibility(View.INVISIBLE);

        // Sets up empty button
        mEmptyButton = (Button)v.findViewById(R.id.empty_browse_host_party);
        mEmptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostParty();
            }
        });

        // Gets reference to progress bar
        mProgressBar = (LinearLayout)v.findViewById(R.id.browse_list_downloadProgress);

        // Gets reference to content
        mContent = (FrameLayout)v.findViewById(R.id.browse_list_content);

        // Gets reference to listView
        mGridView = (GridView)v.findViewById(android.R.id.list);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), PartyDetailsActivity.class);
                i.putExtra(PartyDetailsFragment.EXTRA_PARTY, ((Party)mGridView.getAdapter().getItem(position)).getId());
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sets list view attr
        // TODO clean up
        //mGridView.setDivider(null);
        mGridView.setSelector(android.R.color.transparent);
        mGridView.setVerticalScrollBarEnabled(false);

        // Resets list adapter and gets parties
        mGridView.setAdapter(null);
        getHostedParties();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.browse_party_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                // TODO implement sort
                return true;
            case R.id.action_refresh:
                // Resets list adapter and gets parties
                mGridView.setAdapter(null);
                getHostedParties();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hostParty() {
        // Starts CreatePartyActivity
        Intent i = new Intent(getActivity(), CreatePartyActivity.class);
        startActivity(i);
    }

    private void getHostedParties() {
        // Sets the progress bar to spin until query is done
        mProgressBar.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);

        // Queries for all parties
        ParseQuery<Party> query = ParseQuery.getQuery(Party.class);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<Party>() {
            public void done(List<Party> partyList, ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                mContent.setVisibility(View.VISIBLE);
                if (e == null) {
                    if (partyList.isEmpty()) {
                        mGridView.setVisibility(View.INVISIBLE);
                        mEmptyLayout.setVisibility(View.VISIBLE);
                        return;
                    }

                    ArrayList<Party> tmp = new ArrayList<Party>(partyList);

                    if (getActivity() == null) return;

                    PartyCardAdapter adapter = new PartyCardAdapter(getActivity(), tmp);
                    mGridView.setAdapter(adapter);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

}
