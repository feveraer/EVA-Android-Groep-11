package com.groep11.eva_app.data;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestEvaContract extends AndroidTestCase {

    private static final long TEST_CHALLENGE_ID = 1419033600L;

    public void testBuildChallengeUri() {
        Uri challengeUri = EvaContract.ChallengeEntry.buildChallengeUri(TEST_CHALLENGE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in testBuildChallengeUri in EvaContract.",
                challengeUri);
        assertEquals("Error: Challenge id not properly appended to the end of the Uri",
                new Long(TEST_CHALLENGE_ID).toString(), challengeUri.getLastPathSegment());
        assertEquals("Error: Challenge Uri doesn't match our expected result",
                challengeUri.toString(),
                "content://com.groep11.eva_app/challenge/1419033600");
    }

    public void testBuildCurrentChallengeUri() {
        Uri challengeUri = EvaContract.ChallengeEntry.buildCurrentChallengeUri();
        assertNotNull("Error: Null Uri returned.  You must fill-in testBuildCurrentChallengeUri in EvaContract.",
                challengeUri);
        assertEquals("Error: Challenge id not properly appended to the end of the Uri",
                "current", challengeUri.getLastPathSegment());
        assertEquals("Error: Challenge Uri doesn't match our expected result",
                challengeUri.toString(),
                "content://com.groep11.eva_app/challenge/current");
    }

    public void testBuildCurrentCategoriesUri() {
        Uri categoriesUri = EvaContract.ChallengeEntry.buildCurrentCategoriesUri();
        assertNotNull("Error: Null Uri returned.  You must fill-in testBuildCurrentCategoriesUri in EvaContract.",
                categoriesUri);
        assertEquals("Error: Categories id not properly appended to the end of the Uri",
                "current_categories", categoriesUri.getLastPathSegment());
        assertEquals("Error: Categories Uri doesn't match our expected result",
                categoriesUri.toString(),
                "content://com.groep11.eva_app/challenge/current_categories");
    }
}
