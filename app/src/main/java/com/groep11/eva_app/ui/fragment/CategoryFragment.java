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

    @Bind(R.id.category_text_one)
    TextView mTextOne;
    @Bind(R.id.category_text_two)
    TextView mTextTwo;
    @Bind(R.id.category_text_three)
    TextView mTextThree;

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
                mTextOne.setText(data.getString(COL_CHALLENGE_TITLE));
                mButtonOne.setText(data.getString(COL_CHALLENGE_CATEGORY));
                data.moveToNext();
                //2
                mTextTwo.setText(data.getString(COL_CHALLENGE_TITLE));
                mButtonTwo.setText(data.getString(COL_CHALLENGE_CATEGORY));
                data.moveToNext();
                //3
                mTextThree.setText(data.getString(COL_CHALLENGE_TITLE));
                mButtonThree.setText(data.getString(COL_CHALLENGE_CATEGORY));
            } else {
                Log.e(TAG, "We need 3 challenges!");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @OnClick(R.id.category_button_one)
    public void onCategoryOneClick(View view) {
        mTextOne.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.category_button_two)
    public void onCategoryTwoClick(View view) {
        mTextTwo.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.category_button_three)
    public void onCategoryThreeClick(View view) {
        mTextThree.setVisibility(View.VISIBLE);
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
