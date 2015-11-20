package com.groep11.eva_app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.groep11.eva_app.data.EvaContract.ChallengeEntry;

public class EvaDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 7;

    static final String DATABASE_NAME = "challenge.db";

    public EvaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CHALLENGE_TABLE = "CREATE TABLE " + ChallengeEntry.TABLE_NAME + " (" +
                ChallengeEntry._ID + " INTEGER PRIMARY KEY," +
                ChallengeEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ChallengeEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ChallengeEntry.COLUMN_DIFFICULTY + " TEXT NOT NULL, " +
                ChallengeEntry.COLUMN_REMOTE_TASK_ID + " INTEGER NOT NULL, " +
                ChallengeEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                ChallengeEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                ChallengeEntry.COLUMN_STATUS + " INTEGER NOT NULL DEFAULT 0, " +
                ChallengeEntry.COLUMN_STATUS_CHANGED + " INTEGER NOT NULL DEFAULT 0 " +
                " );";

        db.execSQL(SQL_CREATE_CHALLENGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ChallengeEntry.TABLE_NAME);
        onCreate(db);
    }
}
