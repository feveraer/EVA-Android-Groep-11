package com.groep11.eva_app.ui.fragment;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract;
import com.groep11.eva_app.service.EvaSyncAdapter;
import com.groep11.eva_app.ui.fragment.interfaces.IColumnConstants;
import com.groep11.eva_app.util.ChallengeEntryAdapter;

public class TimelineFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, IColumnConstants {
    public static final String TAG = "TIMELINE";
    private static final int LOADER = 99;

    private ChallengeEntryAdapter entryAdapter;

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment newInstance() {
        TimelineFragment fragment = new TimelineFragment();
        return  fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        EvaSyncAdapter.logAllInDatabase(getActivity());
        Log.d("TIMELINE LOADER", "timeline");
        CursorLoader loader = new CursorLoader(
                this.getActivity(),
                EvaContract.ChallengeEntry.buildCompletedChallenges(), //TODO: only completed challenges URI
                TABLE_COLUMNS,
                null,
                null,
                EvaContract.ChallengeEntry.COLUMN_DATE + " DESC"
        );

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished() called with: " + "loader = [" + loader + "], data = [" + data + "]");
        Log.d(TAG, "onLoadFinished: data amount = " + data.getCount());
        entryAdapter = new ChallengeEntryAdapter(getActivity(), data, 0);
        setListAdapter(entryAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
