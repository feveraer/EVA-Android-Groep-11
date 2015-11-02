package com.groep11.eva_app.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    // These indices are tied to CATEGORY_COLUMNS. If CATEGORY_COLUMNS changes, these
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

    private ImageView mCurrentCategoryIcon;
    private AnimatorSet selectionAnimation;

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
        updateChallengeStatus(mButtonOne.getText().toString(), 1);
    }

    @OnClick(R.id.category_one_cancel)
    public void onChallengeOneCancel(View view) {
        updateChallengeStatus(mButtonOne.getText().toString(), 0);
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
        updateChallengeStatus(mButtonTwo.getText().toString(), 1);
    }

    @OnClick(R.id.category_two_cancel)
    public void onChallengeTwoCancel(View view) {
        updateChallengeStatus(mButtonTwo.getText().toString(), 0);
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
        updateChallengeStatus(mButtonThree.getText().toString(), 1);
    }

    @OnClick(R.id.category_three_cancel)
    public void onChallengeThreeCancel(View view) {
        updateChallengeStatus(mButtonThree.getText().toString(), 0);
        mCategoryThreePreview.setVisibility(View.GONE);
    }

    private void updateChallengeStatus(String category, int status) {
        ContentValues updateValues = new ContentValues();

        String[] selectionArgs = {category};

        updateValues.put(EvaContract.ChallengeEntry.COLUMN_STATUS, status);

        int rowsUpdated = getActivity().getContentResolver().update(
                mUri,
                updateValues,
                null,           // ContentProvider takes care of this
                selectionArgs   // Which category passed on to ContentProvider
        );

        Toast.makeText(getActivity(), "Updated " + rowsUpdated + " rows!", Toast.LENGTH_SHORT).show();
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

    @OnClick({R.id.category_1, R.id.category_2, R.id.category_3})
    public void selectCategory(View view){
        // Only allow the user to select a new category if the animation is done
        // and he's not selecting the same category
        boolean isAllowedToSelect = isAnimationAllowed() &&  mCurrentCategoryIcon != view;

        if(isAllowedToSelect){
            // If there's a category selected, reverse that animation
            if(mCurrentCategoryIcon != null)
                selectCategoryAnimation(mCurrentCategoryIcon, true);

            // Execute the selection animation for a category icon
            selectCategoryAnimation((ImageView) view, false);
        }
    }

    private boolean isAnimationAllowed(){
        // Nothing selected yet, so yeah :)
        if(selectionAnimation == null) return true;
        // Something is selected, only when it's not still animating!
        return !selectionAnimation.isRunning();
    }

    private void selectCategoryAnimation(ImageView imageView, boolean isReversed){
        // Get the current Y position of the imageView, to use it as start position
        float currentY = imageView.getY();

        // Set values for translation and scaling depending on isReversed
        float translateValue = isReversed ? 250 : -250;
        float scaleValue = isReversed ? 1.0f : 1.35f;

        // Create animators for the above values
        ObjectAnimator animateTranslateY = ObjectAnimator.ofFloat(imageView, "y", currentY + translateValue);
        ObjectAnimator animateScaleX = ObjectAnimator.ofFloat(imageView, "scaleX", scaleValue);
        ObjectAnimator animateScaleY = ObjectAnimator.ofFloat(imageView, "scaleY", scaleValue);

        // Create a set for the animators and play the animation as one
        selectionAnimation = new AnimatorSet();
        selectionAnimation.playTogether(animateTranslateY, animateScaleX, animateScaleY);
        selectionAnimation.setDuration(600);

        selectionAnimation.start();

        // Set the currently selected category icon, we'll be able to reverse it's animation later on
        mCurrentCategoryIcon = isReversed ? null : imageView;
    }
}
