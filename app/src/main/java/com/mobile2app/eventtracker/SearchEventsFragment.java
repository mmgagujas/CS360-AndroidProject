package com.mobile2app.eventtracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Search events screen for browsing events.
 *
 */
public class SearchEventsFragment extends Fragment {

    public SearchEventsFragment() {
        // empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_events, container, false);
    }
}