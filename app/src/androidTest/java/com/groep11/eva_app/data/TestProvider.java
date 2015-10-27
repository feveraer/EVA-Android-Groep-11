package com.groep11.eva_app.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.groep11.eva_app.data.EvaContract.ChallengeEntry;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                ChallengeEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ChallengeEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Challenge table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // EvaProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                EvaProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: EvaProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + EvaContract.CONTENT_AUTHORITY,
                    providerInfo.authority, EvaContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: EvaProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /**
     * This test doesn't touch the database.  It verifies that the ContentProvider returns
     * the correct type for each type of URI that it can handle.
     */
    public void testGetType() {
        // content://com.groep11.eva_app/challenge/
        String type = mContext.getContentResolver().getType(ChallengeEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.groep11.eva_app/challenge
        assertEquals("Error: the ChallengeEntry CONTENT_URI should return ChallengeEntry.CONTENT_TYPE",
                ChallengeEntry.CONTENT_TYPE, type);

        long testId = 94074L;
        // content://com.groep11.eva_app/challenge/94074
        type = mContext.getContentResolver().getType(ChallengeEntry.buildChallengeUri(testId));
        // vnd.android.cursor.dir/com.groep11.eva_app/challenge
        assertEquals("Error: the ChallengeEntry CONTENT_URI with id should return ChallengeEntry.CONTENT_TYPE",
                ChallengeEntry.CONTENT_TYPE, type);

        // content://com.groep11.eva_app/challenge/current
        type = mContext.getContentResolver().getType(
                ChallengeEntry.buildCurrentChallengeUri());
        // vnd.android.cursor.dir/com.groep11.eva_app/challenge
        assertEquals("Error: the ChallengeEntry CONTENT_URI with current should return ChallengeEntry.CONTENT_TYPE",
                ChallengeEntry.CONTENT_TYPE, type);
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Use this test to see if the basic challenge query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicChallengeQuery() {
        // insert our test records into the database
        EvaDbHelper dbHelper = new EvaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues challengeValues = TestUtilities.createDummyChallengeValues();
        long challengeRowId = db.insert(ChallengeEntry.TABLE_NAME, null, challengeValues);
        assertTrue("Unable to Insert ChallengeEntry into the Database", challengeRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor challengeCursor = mContext.getContentResolver().query(
                ChallengeEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicChallengeQuery", challengeCursor, challengeValues);
    }

    /**
     * Make sure we can still delete after adding/updating stuff
     * It relies on insertions with testInsertReadProvider.
     */
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createDummyChallengeValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ChallengeEntry.CONTENT_URI, true, tco);
        Uri challengeUri = mContext.getContentResolver().insert(ChallengeEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  If this fails, the insert challenge
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long challengeRowId = ContentUris.parseId(challengeUri);

        // Verify we got a row back.
        assertTrue(challengeRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ChallengeEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating ChallengeEntry.",
                cursor, testValues);
    }

    /**
     * Make sure we can still delete after adding/updating stuff
     * It relies on insertions with testInsertReadProvider.
     */
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our challenge delete.
        TestUtilities.TestContentObserver challengeObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ChallengeEntry.CONTENT_URI, true, challengeObserver);

        deleteAllRecordsFromProvider();

        challengeObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(challengeObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues[] createBulkInsertChallengeValues(int amount) {
        ContentValues[] returnContentValues = new ContentValues[amount];

        for (int i = 0; i < amount; i++) {
            returnContentValues[i] = TestUtilities.createDummyChallengeValuesWithIndex(i);
        }
        return returnContentValues;
    }

    /**
     * Note that this test will work with the built-in (default) provider implementation, which just
     * inserts records one-at-a-time, so really do implement the BulkInsert ContentProvider function.
     */
    public void testBulkInsert() {
        ContentValues[] bulkInsertContentValues = bulkInsert(BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ChallengeEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                ChallengeEntry.COLUMN_DIFFICULTY + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating ChallengeEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    private ContentValues[] bulkInsert(int amount) {
        // Now we can bulkInsert some challenges.  In fact, we only implement BulkInsert for challenge
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertChallengeValues(amount);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver challengeObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ChallengeEntry.CONTENT_URI, true, challengeObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(ChallengeEntry.CONTENT_URI, bulkInsertContentValues);

        challengeObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(challengeObserver);

        assertEquals(insertCount, amount);
        return bulkInsertContentValues;
    }

    public void testQueryChallengeLength() {
        bulkInsert(BULK_INSERT_RECORDS_TO_INSERT);
        int amount = calculateCursorAmount(ChallengeEntry.CONTENT_URI);
        assertEquals(BULK_INSERT_RECORDS_TO_INSERT, amount);
    }

    public void testQueryCurrentChallengeLength() {
        bulkInsert(BULK_INSERT_RECORDS_TO_INSERT);
        int amount = calculateCursorAmount(ChallengeEntry.buildCurrentChallengeUri());
        assertTrue(amount < 2);
    }

    public void testQueryChallengeByIdLength() {
        long id = 2L;
        bulkInsert(BULK_INSERT_RECORDS_TO_INSERT);
        int amount = calculateCursorAmount(ChallengeEntry.buildChallengeUri(id));
        assertEquals(1, amount);
    }

    public void testQueryChallengeById() {
        long id = 2L;
        ContentValues valuesForId = bulkInsert(BULK_INSERT_RECORDS_TO_INSERT)[(int) id - 1];

        Cursor cursor = mContext.getContentResolver().query(
                ChallengeEntry.buildChallengeUri(id),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                ChallengeEntry.COLUMN_DIFFICULTY + " ASC"  // sort order == by DATE ASCENDING
        );
        cursor.moveToFirst();
        TestUtilities.validateCurrentRecord("testQueryChallengeById.  Error validating Challenge.", cursor, valuesForId);
        cursor.close();
    }

    public void testQueryCurrentChallenge() {
        ContentValues values = TestUtilities.createChosenContentValues("Current 1, chosen", "Cat 1", false);
        getContext().getContentResolver().insert(ChallengeEntry.CONTENT_URI, values);
        bulkInsert(BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                ChallengeEntry.buildCurrentChallengeUri(),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                ChallengeEntry.COLUMN_DIFFICULTY + " ASC"  // sort order == by DATE ASCENDING
        );
        cursor.moveToFirst();
        TestUtilities.validateCurrentRecord("testQueryCurrentChallenge.  Error validating Challenge.", cursor, values);
        cursor.close();
    }

    private int calculateCursorAmount(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                ChallengeEntry.COLUMN_DIFFICULTY + " ASC"  // sort order == by DATE ASCENDING
        );
        int cursorCounter = 0;
        while (cursor.moveToNext()) {
            cursorCounter++;
        }
        cursor.close();
        return cursorCounter;
    }
}
