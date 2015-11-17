package com.groep11.eva_app.data.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.groep11.eva_app.R;
import com.groep11.eva_app.ui.fragment.CategoryFragment;
import com.groep11.eva_app.ui.fragment.LoginFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    public static final String ARG_ACCOUNT_TYPE = "ARG_ACCOUNT_TYPE";
    public static final String ARG_AUTH_TYPE = "ARG_AUTH_TYPE";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "ARG_IS_ADDING_NEW_ACCOUNT";
    private static final String PARAM_USER_PASS = "PARAM_USER_PASS";
    private static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";
    private static ServerAuthenticate sServerAuthenticate = ServerAuthenticate.getInstance();
    private String mAuthTokenType = "mAuthTokenType";
    private AccountManager mAccountManager;


    @Bind(R.id.input_login_mail)
    EditText mInputMail;
    @Bind(R.id.input_login_password)
    EditText mInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.bind(this);

        // Add Registration fragment to our fragment container
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        LoginFragment loginFragment = LoginFragment.newInstance();

        fragmentTransaction.add(R.id.fragment_registration_container, loginFragment, CategoryFragment.TAG);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.btn_login_login)
    public void submit() {
        final String userName = mInputMail.getText().toString();
        final String userPass = mInputPassword.getText().toString();
        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                String authtoken = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);
                final Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken);
                res.putExtra(PARAM_USER_PASS, userPass);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                finishLogin(intent);
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
