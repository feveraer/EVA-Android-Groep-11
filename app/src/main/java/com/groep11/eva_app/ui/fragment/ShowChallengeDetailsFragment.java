package com.groep11.eva_app.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.groep11.eva_app.R;
import com.groep11.eva_app.ui.fragment.interfaces.ILoaderFragment;
import com.groep11.eva_app.util.TaskStatus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShowChallengeDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ILoaderFragment {
    OnDetailsSaveCategoryListener mCallback;

    public static final String DETAIL_URI = "URI";
    public static final String PREVIEW = "PREVIEW";
    public static final String TAG = "SHOW_CHALLENGE_DETAILS";

    private static final String CATEGORY_PREFIX = "category_";
    private static final float LEAF_DISABLED_OPACITY = 0.5f;

    private Uri mUri;

    @Bind(R.id.text_challenge_title) TextView mTitleView;
    @Bind(R.id.text_challenge_description) TextView mDescriptionView;
    @Bind(R.id.circle_challenge_image) CircleImageView mCircleImageView;
    @Bind({R.id.image_leaf_1, R.id.image_leaf_2, R.id.image_leaf_3}) List<ImageView> mDifficultyViews;
    @Bind(R.id.details_button_save) Button mSaveButton;

    public ShowChallengeDetailsFragment() {
        // Required empty public constructor
    }

    public static ShowChallengeDetailsFragment newInstance() {
        ShowChallengeDetailsFragment fragment = new ShowChallengeDetailsFragment();
        return fragment;
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

        Log.i(TAG, "args" + arguments + arguments.getBoolean(PREVIEW));
        // If this is the daily preview, show the save category button
        if (arguments != null && !arguments.getBoolean(PREVIEW)) {
            mSaveButton.setVisibility(View.GONE);
        }

        // Show the action bar for navigation
        getActivity().getActionBar().show();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

        try{
            mCallback = (OnDetailsSaveCategoryListener) getFragmentManager().findFragmentByTag(CategoryFragment.TAG);
        } catch (ClassCastException ex) {
            throw new ClassCastException("ShowChallengeDetailsFragment may not be started from another fragment than ShowChallengeFragment");
        }

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
            String challengeTitle = data.getString(COL_CHALLENGE_TITLE);
            String challengeDescription = data.getString(COL_CHALLENGE_DESCRIPTION);
            String challengeDifficulty = data.getString(COL_CHALLENGE_DIFFICULTY);

            mTitleView.setText(challengeTitle);
            mDescriptionView.setText(Html.fromHtml(challengeDescription));
            setCategoryIcon(data.getString(COL_CHALLENGE_CATEGORY).toLowerCase());
            setLeavesOpacity(Integer.parseInt(challengeDifficulty));
        } else {
            //the cursor is empty, so fill the views with their default representations
            mTitleView.setText(R.string.challenge_title);
            mDescriptionView.setText(R.string.challenge_description_long);
            setLeavesOpacity(R.string.challenge_difficulty_middle);
        }
    }

    @OnClick(R.id.details_button_save)
    public void saveSelectedCategory(View view){
        // Navigate back to Category Fragment
        getActivity().getActionBar().hide();
        getFragmentManager().popBackStack();

        // Notify Category fragment that the user wants to do this challenge
        mCallback.onDetailsSaveCategory();
    }

    private void setLeavesOpacity(int diff) {
        //Set opacity leaf #3
        mDifficultyViews.get(2).setAlpha(diff < 3 ? LEAF_DISABLED_OPACITY : 1);
        //Set opacity leaf #2
        mDifficultyViews.get(1).setAlpha(diff < 2 ? LEAF_DISABLED_OPACITY : 1);
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

    public interface OnDetailsSaveCategoryListener {
        public void onDetailsSaveCategory();
    }
}
