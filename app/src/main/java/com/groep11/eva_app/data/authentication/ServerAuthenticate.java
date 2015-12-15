package com.groep11.eva_app.data.authentication;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.groep11.eva_app.data.remote.EvaApiService;
import com.groep11.eva_app.data.remote.ServiceGenerator;
import com.groep11.eva_app.data.remote.TokenResponse;
import com.groep11.eva_app.data.remote.User;

import java.io.IOException;

import retrofit.Call;
import retrofit.Response;

public class ServerAuthenticate {
    private static final String LOG_TAG = ServerAuthenticate.class.getSimpleName();
    private static ServerAuthenticate instance;

    public static ServerAuthenticate getInstance() {
        if (instance == null) {
            instance = new ServerAuthenticate();
        }
        return instance;
    }

    private String[] getToken(Call<TokenResponse> call, String failureLocation){
        Response<TokenResponse> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        if (response == null || !response.isSuccess()) {
            Log.e(LOG_TAG, failureLocation + " call to api didn't work, response == null or !isSuccess : response=" + response);
            return null;
        }
        TokenResponse tokenResponse = response.body();
        if (tokenResponse == null) {
            Log.e(LOG_TAG, "TokenResponse is null");
            return null;
        }

        String token = tokenResponse.getToken();
        Log.v(LOG_TAG, "TokenResponse: " + tokenResponse);

        String userId = tokenResponse.getUserId();
        Log.v(LOG_TAG, "UserId: " + userId);

        if (TextUtils.isEmpty(token)) {
            Log.w(LOG_TAG, "Token from server is empty");
            return null;
        }



        return new String[]{token, userId};
    }

    String[] userSignIn(String userName, String userPass, String mAuthTokenType) {
        EvaApiService api = ServiceGenerator.createService(EvaApiService.class);
        Call<TokenResponse> call = api.getToken(new User(userName, userPass));
        return getToken(call, "userSignIn");
    }

    String[] userSignUp(String userName, String userPass, String mAuthTokenType) {
        EvaApiService api = ServiceGenerator.createService(EvaApiService.class);
        Call<TokenResponse> call = api.register(new User(userName, userPass));
        return getToken(call, "userSignUp");
    }
}
