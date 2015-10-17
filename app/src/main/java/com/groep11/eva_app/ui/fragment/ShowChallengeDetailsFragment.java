package com.groep11.eva_app.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract.ChallengeEntry;
import com.groep11.eva_app.data.remote.Challenge;
import com.groep11.eva_app.data.remote.EvaApiService;
import com.groep11.eva_app.data.remote.Task;
import com.groep11.eva_app.util.DateConversion;

import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
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

    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mDifficultyView;

    public ShowChallengeDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ShowChallengeDetailsFragment.DETAIL_URI);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_challenge_details, container, false);
        mTitleView = (TextView) rootView.findViewById(R.id.text_challenge_title);
        mDescriptionView = (TextView) rootView.findViewById(R.id.text_challenge_description);
        mDifficultyView = (TextView) rootView.findViewById(R.id.text_challenge_difficulty);
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
            mDifficultyView.setText(challengeDifficulty);
        } else {
            //the cursor is empty, so fill the views with their default representations
            mTitleView.setText(R.string.challenge_title_default);
            mDescriptionView.setText(R.string.challenge_description_default);
            mDifficultyView.setText(R.string.challenge_difficulty_default);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
