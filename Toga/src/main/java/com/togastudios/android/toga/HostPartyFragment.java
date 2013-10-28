package com.togastudios.android.toga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HostPartyFragment extends ListFragment {
    private static final String TAG = "HostPartyFragment";

    private Button mEmptyButton;
    private LinearLayout mProgressBar;
    private FrameLayout mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_host_party_list, container, false);

        // Get references to view elements
        mEmptyButton = (Button)v.findViewById(R.id.empty_host_party);
        mEmptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostParty();
            }
        });

        mProgressBar = (LinearLayout)v.findViewById(R.id.host_list_downloadProgress);

        mContent = (FrameLayout)v.findViewById(R.id.host_list_content);

        ListView listView = (ListView)v.findViewById(android.R.id.list);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO clean up
        getListView().setDivider(null);
        getListView().setDividerHeight(12);
        getListView().setSelector(android.R.color.transparent);
        getListView().setVerticalScrollBarEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(null);
        getHostedParties();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.host_party_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_host_party:
                hostParty();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), PartyDetailsActivity.class);
        i.putExtra(PartyDetailsFragment.EXTRA_PARTY, ((Party)getListAdapter().getItem(position)).getId());
        startActivity(i);
    }

    private void hostParty() {
        Intent i = new Intent(getActivity(), CreatePartyActivity.class);
        startActivity(i);
    }

    private void getHostedParties() {
        // Sets progress bar until query completes
        mProgressBar.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);

        // Get the parties the host is hosting
        ParseQuery<Party> query = ParseQuery.getQuery(Party.class);
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.whereEqualTo(Party.hostKey, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Party>() {
            public void done(List<Party> partyList, ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                mContent.setVisibility(View.VISIBLE);
                if (e == null) {
                    if (getActivity() == null) return;

                    ArrayList<Party> tmp = new ArrayList<Party>(partyList);
                    PartyCardAdapter adapter = new PartyCardAdapter(getActivity(), tmp);
                    setListAdapter(adapter);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

}
