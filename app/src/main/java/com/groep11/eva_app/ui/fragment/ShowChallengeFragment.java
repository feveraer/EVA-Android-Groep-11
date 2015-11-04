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
import android.widget.RelativeLayout;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class ShowChallengeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String URI = "URI";
    public static final String TAG = "SHOW_CHALLENGE";
    public static final String BUNDLE_CHALLENGE_ID = "challenge_id";
    private static final String CATEGORY_PREFIX = "category_";

    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            EvaContract.ChallengeEntry.TABLE_NAME + "." + EvaContract.ChallengeEntry._ID,
            EvaContract.ChallengeEntry.COLUMN_TITLE,
            EvaContract.ChallengeEntry.COLUMN_DESCRIPTION,
            EvaContract.ChallengeEntry.COLUMN_DIFFICULTY,
            EvaContract.ChallengeEntry.COLUMN_CATEGORY
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_CHALLENGE_ID = 0;
    public static final int COL_CHALLENGE_TITLE = 1;
    public static final int COL_CHALLENGE_DESCRIPTION = 2;
    public static final int COL_CHALLENGE_DIFFICULTY = 3;
    public static final int COL_CHALLENGE_CATEGORY = 4;

    private final float LEAF_DISABLED_OPACITY = 0.5f;
    private String category;

    //Field binding using Butterknife
    @Bind(R.id.text_challenge_title)
    TextView mTitleView;
    @Bind(R.id.fragment_show_challenge_container)
    View mContainer;
    @Bind(R.id.challenge_card_top)
    RelativeLayout mChallengeCardTop;
    @Bind({R.id.image_leaf_1, R.id.image_leaf_2, R.id.image_leaf_3})
    List<ImageView> mDifficultyView;
    @Bind(R.id.text_challenge_description)
    TextView mDescriptionView;
    @Bind(R.id.circle_challenge_image)
    CircleImageView mCircleImageView;

    public ShowChallengeFragment() {
        // Required empty public constructor
    }

    public static ShowChallengeFragment newInstance(){
        return new ShowChallengeFragment();
    }

    public static ShowChallengeFragment newInstance(Uri uri){
        ShowChallengeFragment fragment = new ShowChallengeFragment();

        Bundle args = new Bundle();
        args.putParcelable(URI, uri);
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

        return rootView;
    }

    private void setCategoryIcon() {
        mCircleImageView.setImageResource(getResources().getIdentifier(
                CATEGORY_PREFIX + category,
                "drawable",
                getActivity().getPackageName()));
    }

    private void setCategoryColor() {

    }

    @OnClick(R.id.card_challenge)
    public void showDetailsActivity(View view) {

        // Create arguments (uri)
        Bundle arguments = new Bundle();
        arguments.putParcelable(ShowChallengeDetailsFragment.DETAIL_URI, mUri);

        // Create new challengeDetailsFragment and set it's arguments
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
            transaction.replace(R.id.fragment_container, challengeDetailsFragment, TAG);
        } else {
            // Category fragment is not on the backstack, so we'll replace Progress & Challenge with challengeDetails
            transaction.remove(getFragmentManager().findFragmentByTag(ShowProgressFragment.TAG));
            transaction.remove(getFragmentManager().findFragmentByTag(ShowChallengeFragment.TAG));
            transaction.add(R.id.fragment_container, challengeDetailsFragment, TAG);
        }

        // Adds challengeFragment to backStack
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
            String challengeDescription = data.getString(COL_CHALLENGE_DESCRIPTION);
            String challengeDifficulty = data.getString(COL_CHALLENGE_DIFFICULTY);
            category = data.getString(COL_CHALLENGE_CATEGORY).toLowerCase();
            setCategoryIcon();

            //challengeDescription = challengeDescription.replace("\n", "").substring(0,challengeDescription.indexOf(" ", 96)+1) + "...";

            mTitleView.setText(challengeTitle);
            mDescriptionView.setText(challengeDescription);
            setLeavesOpacity(Integer.parseInt(challengeDifficulty));
        } else {
            // The cursor is empty, so fill the views with their default representations
            mTitleView.setText(R.string.challenge_title);
            mDescriptionView.setText(R.string.challenge_description_short);
            setLeavesOpacity(R.string.challenge_difficulty_middle);
        }
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
