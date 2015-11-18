package com.groep11.eva_app.data.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.groep11.eva_app.R;
import com.groep11.eva_app.service.EvaSyncAdapter;

public class AccountGeneral {
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "full_access";

    // used to communicate async between submit() and finishLogin()
    private static final String PARAM_USER_PASS = "PARAM_USER_PASS";
    private static final String ARG_IS_ADDING_NEW_ACCOUNT = "ARG_IS_ADDING_NEW_ACCOUNT";

    private static ServerAuthenticate sServerAuthenticate = ServerAuthenticate.getInstance();


    public static void submit(final Context context, final String userName, final String userPass, final boolean isAddingNewAccount) {
        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                String authToken;
                if (isAddingNewAccount)
                    authToken = sServerAuthenticate.userSignUp(userName, userPass, AUTHTOKEN_TYPE_FULL_ACCESS);
                else
                    authToken = sServerAuthenticate.userSignIn(userName, userPass, AUTHTOKEN_TYPE_FULL_ACCESS);
                final Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, context.getString(R.string.sync_account_type));
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                res.putExtra(PARAM_USER_PASS, userPass);
                res.putExtra(ARG_IS_ADDING_NEW_ACCOUNT, isAddingNewAccount);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                finishLogin(context, intent);
            }
        }.execute();
    }

    private static void finishLogin(Context context, Intent intent) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        boolean isAddingNewAccount = intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);

        final Account account = new Account(accountName, accountType);

        String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String authtokenType = AUTHTOKEN_TYPE_FULL_ACCESS;

        // TODO: userdata add _id?
        // Creating the account on the device and setting the auth token we got
        accountManager.addAccountExplicitly(account, accountPassword, null);
        // (Not setting the auth token will cause another call to the server to authenticate the user)
        accountManager.setAuthToken(account, authtokenType, authtoken);


        EvaSyncAdapter.onAccountCreated(context, account);
        EvaSyncAdapter.initializeSyncAdapter(context);
    }
}
