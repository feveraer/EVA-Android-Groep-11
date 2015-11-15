package com.groep11.eva_app.ui.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract;
import com.groep11.eva_app.ui.ToggleSwipeViewPager;
import com.groep11.eva_app.util.TaskStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoryFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ILoaderFragment {

    public static final String TAG = "CATEGORY";

    private static final String CATEGORY_ICON_PREFIX = "category_";
    private static final String CATEGORY_TITLE_PREFIX = "title.";
    private static final int NUM_OF_CATEGORIES = 3;

    private Uri mUri;
    private List<String> mDbCategoryTitles = new ArrayList<>();
    private List<Long> mCurrentIds = new LinkedList<>();
    private PagerAdapter mPagerAdapter;
    private AnimatorSet selectionAnimation;
    private View mSelectedContainer;

    @Bind({ R.id.category_1, R.id.category_2, R.id.category_3 }) List<LinearLayout> mCategoryContainers;
    @Bind({ R.id.category_icon_1, R.id.category_icon_2, R.id.category_icon_3 }) List<ImageView> mCategoryIcons;
    @Bind({ R.id.category_title_1, R.id.category_title_2, R.id.category_title_3 }) List<TextView> mCategoryTitles;

    @Bind({R.id.category_button_save}) Button mSaveButton;
    @Bind(R.id.challenge_preview_container) ToggleSwipeViewPager mPreviewsPager;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save our selected category index for recreating the animation when restoring our fragment
        SharedPreferences.Editor editor = this.getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt("selectedCategoryIndex", getClickedCategoryIndex(mSelectedContainer.getId()));
        editor.apply();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

        // Set our selectedContainer variable to the previouslySelected category index (default is 0)
        SharedPreferences prefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        int previousSelected = prefs.getInt("selectedCategoryIndex", 0);
        mSelectedContainer = mCategoryContainers.get(previousSelected);

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    TABLE_COLUMNS,
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

                mCurrentIds.clear();
                do {
                    mCurrentIds.add(data.getLong(COL_CHALLENGE_ID));
                    setCategoryView(data.getPosition(), data.getString(COL_CHALLENGE_CATEGORY).toLowerCase());
                } while (data.moveToNext());

                // Create a new pager adapter which will load the challengePreviewFragments with the right URI's
                mPagerAdapter = new ScreenSlidePagerAdapter(this.getChildFragmentManager());
                // Link the adapter to the Pager and disable swiping
                mPreviewsPager.setAdapter(mPagerAdapter);
                mPreviewsPager.setSwipingEnabled(false);

                // After data is loaded, select the right one (animation bug fix...)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectCategoryAnimation(mSelectedContainer, false);
                        mPreviewsPager.setCurrentItem(getClickedCategoryIndex(mSelectedContainer.getId()));

                        // Change our save button's text
                        updateSaveButtonText();
                    }
                }, 1000);

            } else {
                Log.e(TAG, "We need 3 challenges!");
            }
        }
    }

    // Set the category icon and title to the related one
    private void setCategoryView(int categoryIndex, String categoryTitle){
        mDbCategoryTitles.add(categoryTitle);
        mCategoryTitles.get(categoryIndex).setText(getCorrectCategoryTitle(categoryTitle));
        mCategoryIcons.get(categoryIndex).setImageResource(getCategoryIconForTitle(categoryTitle));
    }

    // Gets the right category icon resource id for the given category title
    private int getCategoryIconForTitle(String categoryTitle){
        int resourceId =  getResources().getIdentifier(
                CATEGORY_ICON_PREFIX + categoryTitle,
                "drawable",
                getActivity().getPackageName());

        Log.i(TAG, "Searched for icon with name " + CATEGORY_ICON_PREFIX + categoryTitle + " found " + resourceId);
        return  resourceId;
    }

    // Temporary localisation TODO: remove or update this method with user story 'Localisation'
    private String getCorrectCategoryTitle(String categoryTitle){
        int resourceId = getResources().getIdentifier(
                CATEGORY_TITLE_PREFIX + categoryTitle,
                "string",
                getActivity().getPackageName());

        Log.i(TAG, "Searched for title with name " + CATEGORY_TITLE_PREFIX + categoryTitle + " found " + resourceId);

        String correctCategoryTitle;

        try{
            correctCategoryTitle = getResources().getString(resourceId);
        } catch (Exception e) {
            correctCategoryTitle = "Categorie";
        }

        return correctCategoryTitle;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @OnClick(R.id.category_button_save)
    public void saveSelectedCategory(View view){
        // Get the index of the category the user clicked on
        int selectedIndex = getClickedCategoryIndex(mSelectedContainer.getId());

        // Change the selected category's challenge to CHOSEN
        updateChallengeStatus(mCurrentIds.remove(selectedIndex), TaskStatus.CHOSEN.value);
        // Change the remaining category's challenges to NONE
        for(Long leftoverIds : mCurrentIds)
            updateChallengeStatus(leftoverIds, TaskStatus.NONE.value);

        mCurrentIds.clear();

        FragmentManager fragmentManager = this.getActivity().getFragmentManager();

        // Create new progress & challenge fragment
        ShowProgressFragment progressFragment = ShowProgressFragment.newInstance();
        ShowChallengeFragment challengeFragment = ShowChallengeFragment.newInstance(false);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace Category fragment with Progress & Challenge fragment
        transaction.remove(fragmentManager.findFragmentByTag(CategoryFragment.TAG));
        transaction.add(R.id.fragment_main_container, progressFragment, ShowProgressFragment.TAG);
        transaction.add(R.id.fragment_main_container, challengeFragment, ShowChallengeFragment.TAG);

        transaction.commit();
    }

    // Sets the save category button text to a dynamic challenge text
    private void updateSaveButtonText(){
        int selectedIndex = getClickedCategoryIndex(mSelectedContainer.getId());
        String newText = String.format("Ga een %s challenge aan!", mCategoryTitles.get(selectedIndex).getText());

        mSaveButton.setText(newText);
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
    }

    @OnClick({R.id.category_1, R.id.category_2, R.id.category_3})
    public void selectCategory(View view){
        // Only allow the user to select a new category if the animation is done
        // and he's not selecting the same category
        boolean isAllowedToSelect = isAnimationAllowed() &&  mSelectedContainer != view;

        if(isAllowedToSelect){
            // Get the index of the category the user clicked on
            int clickedCategory = getClickedCategoryIndex(view.getId());
            // Adapt challenge preview fragment to the currently selected category
            mPreviewsPager.setCurrentItem(clickedCategory, true);

            // If there's a category selected, reverse that animation
            if(mSelectedContainer != null)
                selectCategoryAnimation(mSelectedContainer, true);

            // Execute the selection animation for a category icon
            selectCategoryAnimation(view, false);

            // Change our save button's text
            updateSaveButtonText();
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
        mSelectedContainer = isReversed ? null : categoryView;
    }

    // Regulate the fragments being shown in our ViewPager
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
          super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "mCurrentIds.size: " + mCurrentIds.size());
            Log.d(TAG, "mCurrentIds.get: " + position);

            if(mCurrentIds != null){
                long id = mCurrentIds.get(position);
                Uri uri = EvaContract.ChallengeEntry.buildChallengeUri(id);
                return ShowChallengeFragment.newInstance(uri, true);
            } else {
                return ShowChallengeFragment.newInstance(true);
            }
        }

        @Override
        public int getCount() {
            return NUM_OF_CATEGORIES;
        }
    }
}
