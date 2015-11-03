package com.groep11.eva_app.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract;
import com.groep11.eva_app.ui.ToggleSwipeViewPager;
import com.groep11.eva_app.util.TaskStatus;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "CATEGORY";

    private Uri mUri;

    private static final int CATEGORY_LOADER = 0;
    private static final int NUM_OF_CATEGORIES = 3;

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

    @Bind({ R.id.category_icon_1, R.id.category_icon_2, R.id.category_icon_3 })
    List<ImageView> categoryIcons;

    @Bind({ R.id.category_title_1, R.id.category_title_2, R.id.category_title_3 })
    List<TextView> categoryTitles;

    @Bind(R.id.challenge_preview_container)
    ToggleSwipeViewPager mPreviewsPager;
    private PagerAdapter mPagerAdapter;

    // Necessary to check if the current animation is still running,
    // prevents buggy animations :)
    private AnimatorSet selectionAnimation;

    private View mSelectedCategoryView;
    private List<Long> currentIds = new LinkedList<>();

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
            if (data.getCount() == NUM_OF_CATEGORIES) {

                currentIds.clear();
                do {
                    currentIds.add(data.getLong(COL_CHALLENGE_ID));
                    setCategoryView(data.getPosition(), data.getString(COL_CHALLENGE_CATEGORY));
                } while (data.moveToNext());

                // Create a new pager adapter which will load the challengePreviewFragments with the right URI's
                mPagerAdapter = new ScreenSlidePagerAdapter(this.getChildFragmentManager());
                // Link the adapter to the Pager and disable swiping
                mPreviewsPager.setAdapter(mPagerAdapter);
                mPreviewsPager.setSwipingEnabled(false);

            } else {
                Log.e(TAG, "We need 3 challenges!");
            }
        }
    }

    private void setCategoryView(int categoryIndex, String categoryTitle){
        // TODO: categoryIcons.get(categoryIndex).setBackgroundResource(...);
        // Set the category title under the icon
        categoryTitles.get(categoryIndex).setText(categoryTitle);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @OnClick(R.id.category_button_save)
    public void saveSelectedCategory(View view){
        // Get the index of the category the user clicked on
        int selectedIndex = getClickedCategoryIndex(mSelectedCategoryView.getId());

        // Change the selected category's challenge to CHOSEN
        updateChallengeStatus(currentIds.remove(selectedIndex), TaskStatus.CHOSEN.value);
        // Change the remaining category's challenges to NONE
        for(Long leftoverIds : currentIds)
            updateChallengeStatus(leftoverIds, TaskStatus.NONE.value);

        currentIds.clear();
    }

    private void updateChallengeStatus(Long id, int status) {
        ContentValues updateValues = new ContentValues();

        updateValues.put(EvaContract.ChallengeEntry.COLUMN_STATUS, status);

        int rowsUpdated = getActivity().getContentResolver().update(
                EvaContract.ChallengeEntry.buildChallengeUri(id),
                updateValues,
                null,           // ContentProvider takes care of this
                null
        );

        //TODO: remove for demo!
        Toast.makeText(getActivity(), "Updated " + rowsUpdated + " rows!", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.category_1, R.id.category_2, R.id.category_3})
    public void selectCategory(View view){
        // Only allow the user to select a new category if the animation is done
        // and he's not selecting the same category
        boolean isAllowedToSelect = isAnimationAllowed() &&  mSelectedCategoryView != view;

        if(isAllowedToSelect){
            // Get the index of the category the user clicked on
            int clickedCategory = getClickedCategoryIndex(view.getId());
            // Adapt challenge preview fragment to the currently selected category
            mPreviewsPager.setCurrentItem(clickedCategory, true);

            // If there's a category selected, reverse that animation
            if(mSelectedCategoryView != null)
                selectCategoryAnimation(mSelectedCategoryView, true);

            // Execute the selection animation for a category icon
            selectCategoryAnimation(view, false);
        }
    }

    private int getClickedCategoryIndex(int resourceId){
        // Get the resource name related to the specified resourceId
        String viewResourceName = getResources().getResourceEntryName(resourceId);
        return Integer.valueOf(viewResourceName.substring(9)) - 1;
    }

    private boolean isAnimationAllowed(){
        // Nothing selected yet, so yeah :)
        if(selectionAnimation == null) return true;
        // Something is selected, only when it's not still animating!
        return !selectionAnimation.isRunning();
    }

    private void selectCategoryAnimation(View categoryView, boolean isReversed){
        // Get the current Y position of the categoryView, to use it as start position
        float currentY = categoryView.getY();

        // Set values for translation and scaling depending on isReversed
        float translateValue = isReversed ? 250 : -250;
        float scaleValue = isReversed ? 1.0f : 1.30f;

        // Create animators for the above values
        ObjectAnimator animateTranslateY = ObjectAnimator.ofFloat(categoryView, "y", currentY + translateValue);
        ObjectAnimator animateScaleX = ObjectAnimator.ofFloat(categoryView, "scaleX", scaleValue);
        ObjectAnimator animateScaleY = ObjectAnimator.ofFloat(categoryView, "scaleY", scaleValue);

        // Create a set for the animators and play the animation as one
        selectionAnimation = new AnimatorSet();
        selectionAnimation.playTogether(animateTranslateY, animateScaleX, animateScaleY);
        // Set the duration and interpolation of the animation
        selectionAnimation.setDuration(1000);
        selectionAnimation.setInterpolator(new BounceInterpolator());
        selectionAnimation.start();

        // Set the currently selected category icon, we'll be able to reverse it's animation later on
        mSelectedCategoryView = isReversed ? null : categoryView;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
          super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "currentIds.size: " + currentIds.size());
            Log.d(TAG, "currentIds.get: " + position);

            if(currentIds != null){
                long id = currentIds.get(position);
                Uri uri = EvaContract.ChallengeEntry.buildChallengeUri(id);
                return ShowChallengeFragment.newInstance(uri);
            } else {
                return ShowChallengeFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return NUM_OF_CATEGORIES;
        }
    }
}
