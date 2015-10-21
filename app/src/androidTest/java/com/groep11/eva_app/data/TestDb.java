package com.groep11.eva_app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(EvaDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() throws Exception {
        super.setUp();
        deleteTheDatabase();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteTheDatabase();
    }

    /**
     * Note that this only tests that the Challenge table has the correct columns.
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(EvaContract.ChallengeEntry.TABLE_NAME);

        mContext.deleteDatabase(EvaDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new EvaDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't the challenge entry table
        assertTrue("Error: Your database was created without the challenge entry table",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + EvaContract.ChallengeEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> challengeColumnHashSet = new HashSet<String>();
        challengeColumnHashSet.add(EvaContract.ChallengeEntry._ID);
        challengeColumnHashSet.add(EvaContract.ChallengeEntry.COLUMN_TITLE);
        challengeColumnHashSet.add(EvaContract.ChallengeEntry.COLUMN_DESCRIPTION);
        challengeColumnHashSet.add(EvaContract.ChallengeEntry.COLUMN_DIFFICULTY);
        challengeColumnHashSet.add(EvaContract.ChallengeEntry.COLUMN_REMOTE_TASK_ID);
        challengeColumnHashSet.add(EvaContract.ChallengeEntry.COLUMN_COMPLETED);
        challengeColumnHashSet.add(EvaContract.ChallengeEntry.COLUMN_DATE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            challengeColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required challenge
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required challenge entry columns",
                challengeColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Test that we can insert and query the challenge database. It uses TestUtilities
        to create dummy ContentValues function and ValidateCurrentRecord to assert equality
    */
    public void testChallengeTable() {

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        EvaDbHelper dbHelper = new EvaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createDummyChallengeValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long rowId;
        rowId = db.insert(EvaContract.ChallengeEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                EvaContract.ChallengeEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No Records returned from challenge query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Challenge Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from challenge query",
                cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
    }
}
