package com.groep11.eva_app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.groep11.eva_app.data.EvaContract.ChallengeEntry;
import com.groep11.eva_app.data.remote.TaskStatus;
import com.groep11.eva_app.util.DateConversion;

import java.util.Date;

public class EvaProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EvaDbHelper mOpenHelper;

    static final int CHALLENGE = 100;
    static final int CHALLENGE_CURRENT = 101;
    static final int CHALLENGE_WITH_ID = 102;
    static final int CHALLENGE_CURRENT_CATEGORIES = 103;
    static final int CHALLENGES_TODAY = 104;

    /*
        This UriMatcher will match each URI to the CHALLENGE, CHALLENGE_CURRENT and
        CHALLENGE_WITH_ID integer constants defined above.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EvaContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, EvaContract.PATH_CHALLENGE, CHALLENGE);
        matcher.addURI(authority, EvaContract.PATH_CHALLENGE + "/today", CHALLENGES_TODAY);
        matcher.addURI(authority, EvaContract.PATH_CHALLENGE + "/current", CHALLENGE_CURRENT);
        // Delete?
        matcher.addURI(authority, EvaContract.PATH_CHALLENGE + "/current_categories", CHALLENGE_CURRENT_CATEGORIES);
        matcher.addURI(authority, EvaContract.PATH_CHALLENGE + "/*", CHALLENGE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new EvaDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "challenge/current"
            case CHALLENGE_CURRENT:
                retCursor = getCurrentChallenge(uri, projection, sortOrder);
                break;
            // "challenge/today"
            case CHALLENGES_TODAY:
                retCursor = getChallengesToday(uri, projection, sortOrder);
                break;
            // "challenge/*"
            case CHALLENGE_WITH_ID:
                retCursor = getChallengeById(uri, projection, sortOrder);
                break;
            // "challenge"
            case CHALLENGE:
                retCursor = getChallenge(projection, selection, selectionArgs, sortOrder);
                break;
            // "challenge/current-categories"
            // delete this?
            case CHALLENGE_CURRENT_CATEGORIES:
                retCursor = getCurrentCategories(uri, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getChallenge(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                ChallengeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getChallengeById(Uri uri, String[] projection, String sortOrder) {
        long id = ChallengeEntry.getIdFromUri(uri);
        String selection = ChallengeEntry._ID + " = ? ";
        String[] selectionArgs = new String[]{"" + id};

        return mOpenHelper.getReadableDatabase().query(
                ChallengeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * only returns the task of today with status CHOSEN or COMPLETED
     */
    private Cursor getCurrentChallenge(Uri uri, String[] projection, String sortOrder) {
        String selection = ChallengeEntry.COLUMN_DATE + " = ? " +
                "AND " + ChallengeEntry.COLUMN_STATUS + " != ? ";
        String[] selectionArgs = new String[]{
                DateConversion.formatDate(new Date()),
                "" + TaskStatus.NONE.ordinal()
        };
        return getChallenge(projection, selection, selectionArgs, sortOrder);
    }

    /**
     * select * where date = today
     * @param uri
     * @param projection
     * @param sortOrder
     * @return
     */
    private Cursor getChallengesToday(Uri uri, String[] projection, String sortOrder) {
        String selection = ChallengeEntry.COLUMN_DATE + " = ? ";
        String[] selectionArgs = new String[]{DateConversion.formatDate(new Date())};
        return getChallenge(projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Delete? I don't need this.
     */
    private Cursor getCurrentCategories(Uri uri, String sortOrder) {
        String selection = ChallengeEntry.COLUMN_DATE + " = ? ";
        String[] selectionArgs = new String[]{DateConversion.formatDate(new Date())};

        //Only category column will be returned
        return getChallenge(new String[]{ChallengeEntry.COLUMN_CATEGORY}, selection, selectionArgs, sortOrder);
    }

    /*
        The getType function uses the UriMatcher. Should return for each URI the base URI
        eg. com.groep11.eva_app/challenge/94074 -> com.groep11.eva_app/challenge/
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CHALLENGE:
                return ChallengeEntry.CONTENT_TYPE;
            case CHALLENGE_WITH_ID:
                return ChallengeEntry.CONTENT_TYPE;
            case CHALLENGE_CURRENT:
                return ChallengeEntry.CONTENT_TYPE;
            case CHALLENGE_CURRENT_CATEGORIES:
                return ChallengeEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CHALLENGE: {
                long _id = db.insert(ChallengeEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ChallengeEntry.buildChallengeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case CHALLENGE:
                rowsDeleted = db.delete(ChallengeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CHALLENGE:
                rowsUpdated = db.update(ChallengeEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHALLENGE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ChallengeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
