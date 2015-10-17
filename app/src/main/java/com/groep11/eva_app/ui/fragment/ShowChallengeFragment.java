package com.groep11.eva_app.ui.fragment;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract;
import com.groep11.eva_app.data.remote.Challenge;
import com.groep11.eva_app.data.remote.EvaApiService;
import com.groep11.eva_app.data.remote.Task;
import com.groep11.eva_app.ui.activity.ShowChallengeDetailsActivity;
import com.groep11.eva_app.util.DateConversion;

import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowChallengeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAIL_URI = "URI";

    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            EvaContract.ChallengeEntry._ID,
            EvaContract.ChallengeEntry.COLUMN_TITLE,
            EvaContract.ChallengeEntry.COLUMN_DIFFICULTY,
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_CHALLENGE_ID = 0;
    public static final int COL_CHALLENGE_TITLE = 1;
    public static final int COL_CHALLENGE_DIFFICULTY = 2;

    private TextView mTitleView;
    private TextView mDifficultyView;
    private LinearLayout mContainer;

    public ShowChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUri = EvaContract.ChallengeEntry.buildCurrentChallengeUri();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_challenge, container, false);
        mTitleView = (TextView) rootView.findViewById(R.id.text_challenge_title);
        mDifficultyView = (TextView) rootView.findViewById(R.id.text_challenge_difficulty);
        mContainer = (LinearLayout) rootView.findViewById(R.id.fragment_show_challenge_container);
        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowChallengeDetailsActivity.class)
                        .setData(mUri);
                startActivity(intent);
            }
        });
        // Load the current challenge
        sync();
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
            String challengeDifficulty = data.getString(COL_CHALLENGE_DIFFICULTY);

            mTitleView.setText(challengeTitle);
            mDifficultyView.setText(challengeDifficulty);
        } else {
            //the cursor is empty, so fill the views with their default representations
            mTitleView.setText(R.string.challenge_title_default);
            mDifficultyView.setText(R.string.challenge_difficulty_default);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void sync() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://95.85.59.29:1337/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EvaApiService service = retrofit.create(EvaApiService.class);

        Call<List<Task>> call = service.listRepos("56224ab96dcac34e5e596a35");
        //async request with enqueue
        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Response<List<Task>> response, Retrofit retrofit) {
                List<Task> tasks = response.body();
                for (Task task : tasks) {
                    Log.d("EVA sync", task.toString());
                    Challenge challenge = task.getChallenge();
                    ContentValues values = new ContentValues();
                    values.put(EvaContract.ChallengeEntry.COLUMN_TITLE, challenge.getTitle());
                    values.put(EvaContract.ChallengeEntry.COLUMN_DESCRIPTION, challenge.getDescription());
                    values.put(EvaContract.ChallengeEntry.COLUMN_DIFFICULTY, challenge.getDifficulty());
                    values.put(EvaContract.ChallengeEntry.COLUMN_REMOTE_TASK_ID, 1);
                    values.put(EvaContract.ChallengeEntry.COLUMN_COMPLETED, task.isCompleted());
                    values.put(EvaContract.ChallengeEntry.COLUMN_DATE, task.getDueDate().split("T")[0]);

                    Uri uri = getActivity().getContentResolver().insert(
                            EvaContract.ChallengeEntry.CONTENT_URI,
                            values);
                    //toasts are slow for many challenges
                    //Toast.makeText(getActivity(), "Added challenge to row  " + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                //do something, I don't care
                Toast.makeText(getActivity(), "SYNC DIDN'T WORK D:, alert Brian", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertDummyChallenge() {
        ContentValues values = new ContentValues();
        values.put(EvaContract.ChallengeEntry.COLUMN_TITLE, "dummy title");
        values.put(EvaContract.ChallengeEntry.COLUMN_DESCRIPTION, "dummy description");
        values.put(EvaContract.ChallengeEntry.COLUMN_DIFFICULTY, "dummy difficulty");
        values.put(EvaContract.ChallengeEntry.COLUMN_REMOTE_TASK_ID, 1);
        values.put(EvaContract.ChallengeEntry.COLUMN_COMPLETED, 0);
        values.put(EvaContract.ChallengeEntry.COLUMN_DATE, DateConversion.formatDate(new Date()));
        Uri uri = getActivity().getContentResolver().insert(
                EvaContract.ChallengeEntry.CONTENT_URI,
                values
        );
        Toast.makeText(getActivity(), "Added challenge to row  " + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
    }

    private void clearAllChallenges() {
        int rowsDeleted = getActivity().getContentResolver().delete(
                EvaContract.ChallengeEntry.CONTENT_URI,
                null,
                null
        );
        Toast.makeText(getActivity(), "Deleted " + rowsDeleted + " rows!", Toast.LENGTH_SHORT).show();
    }
}
