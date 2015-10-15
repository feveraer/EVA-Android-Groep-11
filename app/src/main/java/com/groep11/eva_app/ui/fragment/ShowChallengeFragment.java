package com.groep11.eva_app.ui.fragment;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract;
import com.groep11.eva_app.ui.activity.ShowChallengeDetailsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowChallengeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAIL_URI = "URI";

    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            EvaContract.ChallengeEntry._ID,
            EvaContract.ChallengeEntry.COLUMN_TITLE,
            EvaContract.ChallengeEntry.COLUMN_DIFFICULTY,
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_CHALLENGE_ID = 0;
    public static final int COL_CHALLENGE_TITLE = 1;
    public static final int COL_CHALLENGE_DIFFICULTY = 2;

    private TextView mTitleView;
    private TextView mDifficultyView;
    private LinearLayout mContainer;

    public ShowChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUri = EvaContract.ChallengeEntry.buildCurrentChallengeUri();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_challenge, container, false);
        mTitleView = (TextView) rootView.findViewById(R.id.text_challenge_title);
        mDifficultyView = (TextView) rootView.findViewById(R.id.text_challenge_difficulty);
        mContainer = (LinearLayout) rootView.findViewById(R.id.fragment_show_challenge_container);
        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowChallengeDetailsActivity.class)
                        .setData(mUri);
                startActivity(intent);
            }
        });
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null && data.moveToFirst()){
            String challengeTitle = data.getString(COL_CHALLENGE_TITLE);
            String challengeDifficulty = data.getString(COL_CHALLENGE_DIFFICULTY);

            mTitleView.setText(challengeTitle);
            mDifficultyView.setText(challengeDifficulty);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
