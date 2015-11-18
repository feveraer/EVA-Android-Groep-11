package com.groep11.eva_app.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.EvaContract.ChallengeEntry;
import com.groep11.eva_app.data.authentication.AccountGeneral;
import com.groep11.eva_app.data.remote.Category;
import com.groep11.eva_app.data.remote.Challenge;
import com.groep11.eva_app.data.remote.EvaApiService;
import com.groep11.eva_app.data.remote.ServiceGenerator;
import com.groep11.eva_app.data.remote.Task;
import com.groep11.eva_app.ui.activity.RegistrationActivity;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

public class EvaSyncAdapter extends AbstractThreadedSyncAdapter {
    public final static String LOG_TAG = EvaSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the challenges, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private static final String sBaseUrl = "http://95.85.59.29:1337/api/";
    private static final String sUserId = "562ba076ce597a91722bab4c";

    private final AccountManager mAccountManager;

    public EvaSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync for account(" + account.name + ")");
        try {// Get the auth token for the current account
            Log.v(LOG_TAG, "Getting authToken");
            String authToken = mAccountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);

            Log.v(LOG_TAG, "account -> authToken(" + authToken + ")");
            List<Task> download = download(authToken);
            Log.v(LOG_TAG, "Downloaded tasks(" + download.size() + ")");
            updateLocalData(download);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "ERROR: failed to sync: " + e);
        }
    }

    private List<Task> download(String authToken) {
        EvaApiService service = ServiceGenerator.createService(EvaApiService.class, authToken);

        Call<List<Task>> call = service.listRepos(sUserId);

        Response<List<Task>> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        if (response == null || !response.isSuccess()) {
            Log.e(LOG_TAG, "SYNC DIDN'T WORK D:, alert Brian");
            return null;
        }
        return response.body();
    }

    private void updateLocalData(List<Task> tasks) {
        if (tasks == null)
            return;

        // create content values from tasks
        ContentValues[] cvs = new ContentValues[tasks.size()];
        int index = 0;
        for (Task task : tasks) {
            Log.v("EVA sync", task.toString());
            cvs[index] = contentValuesFromTask(task);
            index++;
        }

        // clear all old data
        int deleted = getContext().getContentResolver().delete(ChallengeEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, "Delete before insert. " + deleted + " Deleted");

        // insert in bulk
        int inserted = getContext().getContentResolver().bulkInsert(
                ChallengeEntry.CONTENT_URI,
                cvs);
        Log.d(LOG_TAG, "Sync Complete. " + inserted + " Inserted");
    }

    private static ContentValues contentValuesFromTask(Task task) {
        Challenge challenge = task.getChallenge();
        Category category = challenge.getCategory();
        ContentValues values = new ContentValues();
        values.put(ChallengeEntry.COLUMN_TITLE, challenge.getTitle());
        values.put(ChallengeEntry.COLUMN_DESCRIPTION, challenge.getDescription());
        values.put(ChallengeEntry.COLUMN_DIFFICULTY, challenge.getDifficulty());
        values.put(ChallengeEntry.COLUMN_REMOTE_TASK_ID, 1);
        values.put(ChallengeEntry.COLUMN_CATEGORY, category.getName());
        values.put(ChallengeEntry.COLUMN_STATUS, task.getStatus());
        values.put(ChallengeEntry.COLUMN_DATE, task.getDueDate().split("T")[0]);
        return values;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.sync_account_type));
        if (accounts.length > 1) {
            Log.w(LOG_TAG, "More than one account(" + accounts.length + ") found of type(" + context.getString(R.string.sync_account_type) + "), using first");
        }

        if (accounts.length < 1) {
            // launch RegistrationActivity to add an account
            context.startActivity(new Intent(context, RegistrationActivity.class));
            return null;
        }

        return accounts[0];
    }

    /**
     * [development] temporary method to update the sync account
     */
    public static void deleteAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.sync_account_type));

        for (Account account : accounts) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                boolean success = accountManager.removeAccountExplicitly(account);
                Log.d(LOG_TAG, "Deleted account: " + success);
            }
        }
    }

    public static void onAccountCreated(Context context, Account newAccount) {
        /*
         * Since we've created an account
         */
        EvaSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
