package com.groep11.eva_app.data.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.groep11.eva_app.R;
import com.groep11.eva_app.service.EvaSyncAdapter;

public class AccountGeneral {
    private static final String LOG_TAG = AccountGeneral.class.getSimpleName();
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "full_access";

    // used to communicate async between submit() and finishLogin()
    private static final String PARAM_USER_PASS = "PARAM_USER_PASS";
    public static final String PARAM_USER_ID = "PARAM_USER_ID";
    private static final String ARG_IS_ADDING_NEW_ACCOUNT = "ARG_IS_ADDING_NEW_ACCOUNT";

    private static ServerAuthenticate sServerAuthenticate = ServerAuthenticate.getInstance();

    public static boolean LOGIN = false;
    public static boolean SIGNUP = false;


    public static void submit(final Context context, final String userName, final String userPass, final boolean isAddingNewAccount, final SignInFinishedCallback cb) {
        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                String[] signData = new String[2];

                if (isAddingNewAccount)
                    signData = sServerAuthenticate.userSignUp(userName, userPass, AUTHTOKEN_TYPE_FULL_ACCESS);
                else
                    signData = sServerAuthenticate.userSignIn(userName, userPass, AUTHTOKEN_TYPE_FULL_ACCESS);
                String authToken = signData != null ? signData[0] : "";
                String userId = signData != null ? signData[1] : "";
                final Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, context.getString(R.string.sync_account_type));
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                res.putExtra(PARAM_USER_ID, userId);
                res.putExtra(PARAM_USER_PASS, userPass);
                res.putExtra(ARG_IS_ADDING_NEW_ACCOUNT, isAddingNewAccount);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                finishLogin(context, intent, cb);
            }
        }.execute();
    }

    private static void saveUserIdInPrefs(Context context, String userId) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("com.groep11.eva_app", Context.MODE_PRIVATE);
        prefs.edit().putString(PARAM_USER_ID, userId).apply();
        Log.d("PREFS", "saveUserIdInPrefs: " + userId);
    }

    private static void finishLogin(Context context, Intent intent, SignInFinishedCallback cb) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String userId = intent.getStringExtra(PARAM_USER_ID);

        if(TextUtils.isEmpty(authtoken)){
            cb.signInFinished(null);
            Log.w(LOG_TAG, String.format("Failed to authenticate with server: name(%s) pass(%S) type(%s) token(%s)", accountName, accountPassword, accountType, authtoken));
            return;
        }

        saveUserIdInPrefs(context, userId);

        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        boolean isAddingNewAccount = intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);

        final Account account = new Account(accountName, accountType);

        String authtokenType = AUTHTOKEN_TYPE_FULL_ACCESS;

        // TODO: userdata add _id?
        // TODO: encrypt password
        // Creating the account on the device and setting the auth token we got
        accountManager.addAccountExplicitly(account, accountPassword, null);
        // (Not setting the auth token will cause another call to the server to authenticate the user)
        accountManager.setAuthToken(account, authtokenType, authtoken);


        EvaSyncAdapter.onAccountCreated(context, account);
        EvaSyncAdapter.initializeSyncAdapter(context);

        cb.signInFinished(account);
    }
}
