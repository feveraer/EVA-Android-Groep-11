package com.groep11.eva_app.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Uncomment this class when you are ready to test your UriMatcher.  Note that this class utilizes
    constants that are declared with package protection inside of the UriMatcher, which is why
    the test must be in the same data package as the Android app code.  Doing the test this way is
    a nice compromise between data hiding and testability.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final long TEST_CHALLENGE_ID = 10L;  // December 20th, 2014

    // content://com.groep11.eva_app/challenge"
    private static final Uri TEST_CHALLENGE_DIR = EvaContract.ChallengeEntry.CONTENT_URI;
    // content://com.groep11.eva_app/challenge/:id"
    private static final Uri TEST_CHALLENGE_WITH_ID_DIR = EvaContract.ChallengeEntry.buildChallengeUri(TEST_CHALLENGE_ID);
    // content://com.groep11.eva_app/challenge/current"
    private static final Uri TEST_CHALLENGE_CURRENT_DIR = EvaContract.ChallengeEntry.buildCurrentChallengeUri();

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = EvaProvider.buildUriMatcher();

        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                testMatcher.match(TEST_CHALLENGE_DIR), EvaProvider.CHALLENGE);
        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_CHALLENGE_WITH_ID_DIR), EvaProvider.CHALLENGE_WITH_ID);
        assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.",
                testMatcher.match(TEST_CHALLENGE_CURRENT_DIR), EvaProvider.CHALLENGE_CURRENT);
    }
}
