package com.groep11.eva_app.ui.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract;
import com.groep11.eva_app.service.EvaSyncAdapter;
import com.groep11.eva_app.ui.fragment.interfaces.IColumnConstants;
import com.groep11.eva_app.util.TaskStatus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShowChallengeFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, IColumnConstants {

    public static final String URI = "URI";
    public static final String PREVIEW = "PREVIEW";
    public static final String TAG = "SHOW_CHALLENGE";

    private static final String CATEGORY_PREFIX = "category_";
    private static final float LEAF_DISABLED_OPACITY = 0.5f;

    private String currentChallengeTitle;
    private Uri mUri;
    private Long mCurrentId = 0L;

    @Bind(R.id.text_challenge_title) TextView mTitleView;
    @Bind(R.id.text_challenge_description) TextView mDescriptionView;
    @Bind(R.id.circle_challenge_image) CircleImageView mCircleImageView;
    @Bind({R.id.image_leaf_1, R.id.image_leaf_2, R.id.image_leaf_3}) List<ImageView> mDifficultyView;
    @Bind(R.id.challenge_complete) ImageView mCompleteChallengeView;

    public ShowChallengeFragment() {
        // Required empty public constructor
    }

    public static ShowChallengeFragment newInstance(boolean isPreview){
        ShowChallengeFragment fragment = new ShowChallengeFragment();
        if (isPreview) {
            Bundle args = new Bundle();
            args.putBoolean(PREVIEW, isPreview);
            fragment.setArguments(args);
        }
        return new ShowChallengeFragment();
    }

    public static ShowChallengeFragment newInstance(Uri uri, boolean isPreview){
        ShowChallengeFragment fragment = new ShowChallengeFragment();
        Bundle args = new Bundle();
        args.putParcelable(URI, uri);
        if (isPreview) {
            args.putBoolean(PREVIEW, isPreview);
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(URI);
        } else {
            mUri = EvaContract.ChallengeEntry.buildCurrentChallengeUri();
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_challenge, container, false);
        // Non-activity binding for butterknife
        ButterKnife.bind(this, rootView);

        if (arguments != null && arguments.getBoolean(PREVIEW)) {
            mCompleteChallengeView.setVisibility(View.GONE);
        }

        return rootView;
    }

    private boolean isPreviewChallenge(){
        return mCompleteChallengeView.getVisibility() == View.GONE;
    }

    @OnClick(R.id.card_challenge)
    public void showDetailsActivity(View view) {

        // Create arguments (uri)
        Bundle arguments = new Bundle();
        arguments.putParcelable(ShowChallengeDetailsFragment.DETAIL_URI, mUri);
        arguments.putBoolean(PREVIEW, isPreviewChallenge());

        // Create new challengeDetailsFragment and set it's arguments
        Log.i(TAG, "prev : " + isPreviewChallenge());
        ShowChallengeDetailsFragment challengeDetailsFragment = ShowChallengeDetailsFragment.newInstance();

        challengeDetailsFragment.setArguments(arguments);

        // Get the activity's fragment manager (important for categoryFragment with the viewpager!)
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out,
                android.R.animator.fade_in, android.R.animator.fade_out);

        // Replace current fragments with challengeDetailsFragment
        Fragment categoryFragment = fragmentManager.findFragmentByTag(CategoryFragment.TAG);

        if(categoryFragment != null) {
            // If category fragment is on the backstack, replace it with challengeDetails
            transaction.replace(R.id.fragment_main_container, challengeDetailsFragment, TAG);
        } else {
            // Category fragment is not on the backstack, so we'll replace Progress & Challenge with challengeDetails
            transaction.remove(getFragmentManager().findFragmentByTag(ShowProgressFragment.TAG));
            transaction.remove(getFragmentManager().findFragmentByTag(ShowChallengeFragment.TAG));
            transaction.add(R.id.fragment_main_container, challengeDetailsFragment, TAG);
        }

        // Adds challengeFragment to backStack
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @OnClick(R.id.challenge_complete)
    public void onComplete(View view) {
        // Update challenge status to COMPLETED
        updateChallengeStatus(mCurrentId, TaskStatus.COMPLETED.value);

        // Show Challenge Complete dialog
        showChallengeCompleteDialog();

        // Create Timeline fragment
        TimelineFragment timelineFragment = TimelineFragment.newInstance();

        // Remove current challenge card and add the timeline
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out,
                android.R.animator.fade_in, android.R.animator.fade_out);

        transaction.remove(getFragmentManager().findFragmentByTag(ShowChallengeFragment.TAG));
        transaction.add(R.id.fragment_main_container, timelineFragment, TimelineFragment.TAG);
        transaction.commit();
    }

    // Refactor later? This exact method also exists in CategoryFragment
    private void updateChallengeStatus(Long id, int status) {
        ContentValues updateValues = new ContentValues();

        updateValues.put(EvaContract.ChallengeEntry.COLUMN_STATUS, status);

        int rowsUpdated = getActivity().getContentResolver().update(
                EvaContract.ChallengeEntry.buildChallengeUri(id),
                updateValues,
                null,           // ContentProvider takes care of this
                null
        );
//        EvaSyncAdapter.syncImmediately(getActivity());
    }

    private void showChallengeCompleteDialog() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        ChallengeCompleteDialog dialog = ChallengeCompleteDialog.newInstance("Uitdaging voltooid!", currentChallengeTitle);
        dialog.show(fragmentManager, "fragment_challenge_complete");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER, null, this);
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
                    TABLE_COLUMNS, // Projection to return
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
            mCurrentId = data.getLong(COL_CHALLENGE_ID);

            currentChallengeTitle = data.getString(COL_CHALLENGE_TITLE);
            String challengeDescription = data.getString(COL_CHALLENGE_DESCRIPTION);
            String challengeDifficulty = data.getString(COL_CHALLENGE_DIFFICULTY);

            mTitleView.setText(currentChallengeTitle);
            mDescriptionView.setText(challengeDescription.replace("\n", "").substring(0,
                    challengeDescription.indexOf(" ", 25)+1) + "...");
            setCategoryIcon(data.getString(COL_CHALLENGE_CATEGORY).toLowerCase());
            setLeavesOpacity(Integer.parseInt(challengeDifficulty));
        } else {
            // The cursor is empty, so fill the views with their default representations
            mTitleView.setText(R.string.challenge_title);
            mDescriptionView.setText(R.string.challenge_description_short);
            setLeavesOpacity(R.string.challenge_difficulty_middle);
        }
    }


    private void setCategoryIcon(String category) {
        mCircleImageView.setImageResource(getResources().getIdentifier(
                CATEGORY_PREFIX + category,
                "drawable",
                getActivity().getPackageName()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void setLeavesOpacity(int diff) {
        // Set opacity leaf #3
        mDifficultyView.get(2).setAlpha(diff < 3 ? LEAF_DISABLED_OPACITY : 1);
        // Set opacity leaf #2
        mDifficultyView.get(1).setAlpha(diff < 2 ? LEAF_DISABLED_OPACITY : 1);
    }

    private void sync() {
        EvaSyncAdapter.deleteAccount(getActivity());
        EvaSyncAdapter.syncImmediately(getActivity());
    }
}
