package com.groep11.eva_app.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract.ChallengeEntry;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowChallengeDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAIL_URI = "URI";

    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            ChallengeEntry.TABLE_NAME + "." + ChallengeEntry._ID,
            ChallengeEntry.COLUMN_TITLE,
            ChallengeEntry.COLUMN_DESCRIPTION,
            ChallengeEntry.COLUMN_DIFFICULTY,
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_CHALLENGE_ID = 0;
    public static final int COL_CHALLENGE_TITLE = 1;
    public static final int COL_CHALLENGE_DESCRIPTION = 2;
    public static final int COL_CHALLENGE_DIFFICULTY = 3;

    private final float LEAF_DISABLED_OPACITY = 0.5f;

    @Bind(R.id.text_challenge_title)
    TextView mTitleView;
    @Bind(R.id.text_challenge_description)
    TextView mDescriptionView;

    @Bind({R.id.image_leaf_1, R.id.image_leaf_2, R.id.image_leaf_3})
    List<ImageView> mDifficultyView;

    public ShowChallengeDetailsFragment() {
        // Required empty public constructor
    }

    public static ShowChallengeDetailsFragment newInstance(){
        return new ShowChallengeDetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ShowChallengeDetailsFragment.DETAIL_URI);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_challenge_details, container, false);
        // Non-activity binding for butterknife
        ButterKnife.bind(this, rootView);

        return rootView;
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
        if (data != null && data.moveToFirst()) {
            String challengeTitle = data.getString(COL_CHALLENGE_TITLE);
            String challengeDescription = data.getString(COL_CHALLENGE_DESCRIPTION);
            String challengeDifficulty = data.getString(COL_CHALLENGE_DIFFICULTY);

            mTitleView.setText(challengeTitle);
            mDescriptionView.setText(challengeDescription);
            setLeavesOpacity(Integer.parseInt(challengeDifficulty));
        } else {
            //the cursor is empty, so fill the views with their default representations
            mTitleView.setText(R.string.challenge_title_default);
            mDescriptionView.setText(R.string.challenge_description_default);
            setLeavesOpacity(R.string.challenge_difficulty_default);
        }
    }

    private void setLeavesOpacity(int diff) {
        //Set opacity leaf #3
        mDifficultyView.get(2).setAlpha(diff < 3 ? LEAF_DISABLED_OPACITY : 1);
        //Set opacity leaf #2
        mDifficultyView.get(1).setAlpha(diff < 2 ? LEAF_DISABLED_OPACITY : 1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
