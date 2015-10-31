package com.groep11.eva_app.ui.fragment;

import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "CATEGORY";

    private Uri mUri;

    private static final int CATEGORY_LOADER = 0;

    private static final String[] CATEGORY_COLUMNS = {
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

    @Bind(R.id.category_button_one)
    Button mButtonOne;
    @Bind(R.id.category_button_two)
    Button mButtonTwo;
    @Bind(R.id.category_button_three)
    Button mButtonThree;

    @Bind(R.id.category_one_preview)
    LinearLayout mCategoryOnePreview;
    @Bind(R.id.category_one_challenge_title)
    TextView mChallengeOneTitle;
    @Bind(R.id.category_one_challenge_descr)
    TextView mChallengeOneDescr;
    @Bind(R.id.category_one_confirm)
    Button mChallengeOneConfirm;
    @Bind(R.id.category_one_cancel)
    Button mChallengeOneCancel;

    @Bind(R.id.category_two_preview)
    LinearLayout mCategoryTwoPreview;
    @Bind(R.id.category_two_challenge_title)
    TextView mChallengeTwoTitle;
    @Bind(R.id.category_two_challenge_descr)
    TextView mChallengeTwoDescr;
    @Bind(R.id.category_two_confirm)
    Button mChallengeTwoConfirm;
    @Bind(R.id.category_two_cancel)
    Button mChallengeTwoCancel;

    @Bind(R.id.category_three_preview)
    LinearLayout mCategoryThreePreview;
    @Bind(R.id.category_three_challenge_title)
    TextView mChallengeThreeTitle;
    @Bind(R.id.category_three_challenge_descr)
    TextView mChallengeThreeDescr;
    @Bind(R.id.category_three_confirm)
    Button mChallengeThreeConfirm;
    @Bind(R.id.category_three_cancel)
    Button mChallengeThreeCancel;

    // Temporary button to go to ShowChallengeFragment
    @Bind(R.id.category_button_next)
    Button mButtonNext;


    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUri = EvaContract.ChallengeEntry.buildChallengesTodayURI();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);
        // Non-activity binding for butterknife
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CATEGORY_LOADER, null, this);
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
                    CATEGORY_COLUMNS,
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
            if (data.getCount() == 3) {
                //1
                mButtonOne.setText(data.getString(COL_CHALLENGE_CATEGORY));
                mChallengeOneTitle.setText(data.getString(COL_CHALLENGE_TITLE));
                mChallengeOneDescr.setText(data.getString(COL_CHALLENGE_DESCRIPTION));
                data.moveToNext();
                //2
                mButtonTwo.setText(data.getString(COL_CHALLENGE_CATEGORY));
                mChallengeTwoTitle.setText(data.getString(COL_CHALLENGE_TITLE));
                mChallengeTwoDescr.setText(data.getString(COL_CHALLENGE_DESCRIPTION));
                data.moveToNext();
                //3
                mButtonThree.setText(data.getString(COL_CHALLENGE_CATEGORY));
                mChallengeThreeTitle.setText(data.getString(COL_CHALLENGE_TITLE));
                mChallengeThreeDescr.setText(data.getString(COL_CHALLENGE_DESCRIPTION));
            } else {
                Log.e(TAG, "We need 3 challenges!");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    // 1

    @OnClick(R.id.category_button_one)
    public void onCategoryOneClick(View view) {
        // first set other challenge previews invisible (gone)
        mCategoryTwoPreview.setVisibility(View.GONE);
        mCategoryThreePreview.setVisibility(View.GONE);
        // show challenge preview
        mCategoryOnePreview.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.category_one_confirm)
    public void onChallengeOneConfirm(View view) {

    }

    @OnClick(R.id.category_one_cancel)
    public void onChallengeOneCancel(View view) {
        mCategoryOnePreview.setVisibility(View.GONE);
    }

    // 2

    @OnClick(R.id.category_button_two)
    public void onCategoryTwoClick(View view) {
        // first set other challenge previews invisible (gone)
        mCategoryOnePreview.setVisibility(View.GONE);
        mCategoryThreePreview.setVisibility(View.GONE);
        // show challenge preview
        mCategoryTwoPreview.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.category_two_confirm)
    public void onChallengeTwoConfirm(View view) {

    }

    @OnClick(R.id.category_two_cancel)
    public void onChallengeTwoCancel(View view) {
        mCategoryTwoPreview.setVisibility(View.GONE);
    }

    // 3

    @OnClick(R.id.category_button_three)
    public void onCategoryThreeClick(View view) {
        // first set other challenge previews invisible (gone)
        mCategoryOnePreview.setVisibility(View.GONE);
        mCategoryTwoPreview.setVisibility(View.GONE);
        // show challenge preview
        mCategoryThreePreview.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.category_three_confirm)
    public void onChallengeThreeConfirm(View view) {

    }

    @OnClick(R.id.category_three_cancel)
    public void onChallengeThreeCancel(View view) {
        mCategoryThreePreview.setVisibility(View.GONE);
    }

    @OnClick(R.id.category_button_next)
    public void onNextClick(View view) {

        // Create new progress & challenge fragment
        ShowProgressFragment progressFragment = ShowProgressFragment.newInstance();
        ShowChallengeFragment challengeFragment = ShowChallengeFragment.newInstance();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace current fragments with challengeDetailsFragment
        transaction.remove(getFragmentManager().findFragmentByTag(CategoryFragment.TAG));
        transaction.add(R.id.fragment_container, progressFragment, ShowProgressFragment.TAG);
        transaction.add(R.id.fragment_container, challengeFragment, ShowChallengeFragment.TAG);

        transaction.addToBackStack(null);

        transaction.commit();
    }
}
