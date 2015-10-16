package com.groep11.eva_app.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.groep11.eva_app.data.EvaContract.ChallengeEntry;
import com.groep11.eva_app.util.DateConversion;
import com.groep11.eva_app.utils.PollingCheck;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {
    public static ContentValues createDummyChallengeValues() {
        return createDummyChallengeValuesWithIndex(0);
    }

    public static ContentValues createDummyChallengeValuesWithIndex(int i) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(ChallengeEntry.COLUMN_TITLE, "Challenge " + i);
        weatherValues.put(ChallengeEntry.COLUMN_DESCRIPTION, "Description " + i);
        weatherValues.put(ChallengeEntry.COLUMN_DIFFICULTY, i);
        weatherValues.put(ChallengeEntry.COLUMN_REMOTE_TASK_ID, i * 1000 + i * 100 + i * 10 + i);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, i);
        weatherValues.put(ChallengeEntry.COLUMN_DATE, DateConversion.formatDate(c.getTime()));
        weatherValues.put(ChallengeEntry.COLUMN_COMPLETED, i % 2 == 0);
        return weatherValues;
    }


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(idx);
            if (columnName.equals(ChallengeEntry.COLUMN_COMPLETED)) {
                int actualInt = valueCursor.getInt(idx);
                if (actualInt == 0)
                    actualValue = "false";
                else if (actualInt == 1)
                    actualValue = "true";
            }
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, actualValue);
        }
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
