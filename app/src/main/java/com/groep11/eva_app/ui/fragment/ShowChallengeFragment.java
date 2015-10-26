package com.groep11.eva_app.ui.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract;
import com.groep11.eva_app.service.EvaSyncAdapter;
import com.groep11.eva_app.util.DateConversion;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private final float LEAF_DISABLED_OPACITY = 0.5f;

    //Field binding using Butterknife
    @Bind(R.id.text_challenge_title)
    TextView mTitleView;
    @Bind(R.id.fragment_show_challenge_container)
    View mContainer;
    @Bind({R.id.image_leaf_1, R.id.image_leaf_2, R.id.image_leaf_3})
    List<ImageView> mDifficultyView;

    public ShowChallengeFragment() {
        // Required empty public constructor
    }

    public static ShowChallengeFragment newInstance(){
        return new ShowChallengeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mUri = EvaContract.ChallengeEntry.buildCurrentChallengeUri();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_challenge, container, false);
        // Non-activity binding for butterknife
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.card_challenge)
    public void showDetailsActivity(View view) {

        //Create arguments (uri)
        Bundle arguments = new Bundle();
        arguments.putParcelable(ShowChallengeDetailsFragment.DETAIL_URI, mUri);

        //Create new challengeDetailsFragment and set it's arguments
        ShowChallengeDetailsFragment challengeDetailsFragment = ShowChallengeDetailsFragment.newInstance();
        challengeDetailsFragment.setArguments(arguments);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out,        //Fragment in / out
                                        android.R.animator.fade_in, android.R.animator.fade_out);       //Backstack in / out

        transaction.replace(R.id.fragment_container, challengeDetailsFragment);
        //adds challengeFragment to backStack
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),  // Parent activity context
                    mUri,           // Table to query
                    DETAIL_COLUMNS, // Projection to return
                    null,           // No selection clause
                    null,           // No selection arguments
                    null            // Default sort order
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String challengeTitle = data.getString(COL_CHALLENGE_TITLE);
            String challengeDifficulty = data.getString(COL_CHALLENGE_DIFFICULTY);

            mTitleView.setText(challengeTitle);
            setLeavesOpacity(Integer.parseInt(challengeDifficulty));
        } else {
            //the cursor is empty, so fill the views with their default representations
            mTitleView.setText(R.string.challenge_title_default);
            setLeavesOpacity(R.string.challenge_difficulty_default);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void setLeavesOpacity(int diff) {
        //Set opacity leaf #3
        mDifficultyView.get(2).setAlpha(diff < 3 ? LEAF_DISABLED_OPACITY : 1);
        //Set opacity leaf #2
        mDifficultyView.get(1).setAlpha(diff < 2 ? LEAF_DISABLED_OPACITY : 1);
    }

    private void sync() {
        EvaSyncAdapter.deleteAccount(getActivity());
        EvaSyncAdapter.syncImmediately(getActivity());
    }


    /**
     * Testing methods below, remove later.
     */
    private void insertDummyChallenge() {
        ContentValues values = new ContentValues();
        values.put(EvaContract.ChallengeEntry.COLUMN_TITLE, "dummy title");
        values.put(EvaContract.ChallengeEntry.COLUMN_DESCRIPTION, "dummy description");
        values.put(EvaContract.ChallengeEntry.COLUMN_DIFFICULTY, "dummy difficulty");
        values.put(EvaContract.ChallengeEntry.COLUMN_REMOTE_TASK_ID, 1);
        values.put(EvaContract.ChallengeEntry.COLUMN_COMPLETED, 0);
        values.put(EvaContract.ChallengeEntry.COLUMN_DATE, DateConversion.formatDate(new Date()));
        Uri uri = getActivity().getContentResolver().insert(
                EvaContract.ChallengeEntry.CONTENT_URI,
                values
        );
        Toast.makeText(getActivity(), "Added challenge to row  " + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
    }

    private void clearAllChallenges() {
        int rowsDeleted = getActivity().getContentResolver().delete(
                EvaContract.ChallengeEntry.CONTENT_URI,
                null,
                null
        );
        Toast.makeText(getActivity(), "Deleted " + rowsDeleted + " rows!", Toast.LENGTH_SHORT).show();
    }
}
