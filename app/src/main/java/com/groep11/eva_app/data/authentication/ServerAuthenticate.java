package com.groep11.eva_app.data.authentication;

import android.text.TextUtils;
import android.util.Log;

public class ServerAuthenticate {
    private static final String LOG_TAG = ServerAuthenticate.class.getSimpleName();
    private static ServerAuthenticate instance;
    // TODO: remove
    private static final String authToken = "abcdefg";

    public static ServerAuthenticate getInstance() {
        if(instance == null){
            instance = new ServerAuthenticate();
        }
        return instance;
    }

    String userSignIn(String userName, String userPass, String mAuthTokenType){
        Log.d(LOG_TAG, String.format("name(%s), pass(%s), token(%s)", userName, userPass, mAuthTokenType));
        return TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPass) ? null : authToken;
    }

    String userSignUp(String userName, String userPass, String mAuthTokenType){
        Log.d(LOG_TAG, String.format("name(%s), pass(%s), token(%s)", userName, userPass, mAuthTokenType));
        return TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPass) ? null : authToken;
    }

}
