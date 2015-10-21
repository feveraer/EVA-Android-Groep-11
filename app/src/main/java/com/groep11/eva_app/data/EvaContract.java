package com.groep11.eva_app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tjen on 13/10/15.
 */
public class EvaContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.groep11.eva_app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.groep11.eva_app/challenge/ is a valid path for looking at data
    public static final String PATH_CHALLENGE = "challenge";

    /* Inner class that defines the table contents of the challenge table */
    public static final class ChallengeEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHALLENGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHALLENGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHALLENGE;

        public static final String TABLE_NAME = "challenge";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DIFFICULTY = "difficulty";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_REMOTE_TASK_ID = "task_id";
        public static final String COLUMN_COMPLETED = "completed";

        public static Uri buildChallengeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCurrentChallengeUri() {
            return CONTENT_URI.buildUpon().appendPath("current").build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }
}
