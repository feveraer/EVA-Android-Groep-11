package com.groep11.eva_app.ui.fragment;
import com.groep11.eva_app.data.EvaContract;

public interface ILoaderFragment {
    int LOADER = 0;

    String[] TABLE_COLUMNS = {
            EvaContract.ChallengeEntry.TABLE_NAME + "." + EvaContract.ChallengeEntry._ID,
            EvaContract.ChallengeEntry.COLUMN_TITLE,
            EvaContract.ChallengeEntry.COLUMN_DESCRIPTION,
            EvaContract.ChallengeEntry.COLUMN_DIFFICULTY,
            EvaContract.ChallengeEntry.COLUMN_CATEGORY
    };

    // These indices are tied to TABLE_COLUMNS.  If TABLE_COLUMNS changes, these
    // must change.
    int COL_CHALLENGE_ID = 0;
    int COL_CHALLENGE_TITLE = 1;
    int COL_CHALLENGE_DESCRIPTION = 2;
    int COL_CHALLENGE_DIFFICULTY = 3;
    int COL_CHALLENGE_CATEGORY = 4;
}
