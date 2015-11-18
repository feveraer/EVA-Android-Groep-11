package com.groep11.eva_app.data.authentication;

import android.accounts.Account;

public interface SignInFinishedCallback {
    /**
     * @param account is null if failed, otherwise it is added to the AccountManager with password and token
     */
    void signInFinished(Account account);
}
