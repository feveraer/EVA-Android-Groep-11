package com.groep11.eva_app.data.authentication;

import android.text.TextUtils;
import android.util.Log;

import com.groep11.eva_app.data.remote.EvaApiService;
import com.groep11.eva_app.data.remote.ServiceGenerator;
import com.groep11.eva_app.data.remote.TokenResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Response;

public class ServerAuthenticate {
    private static final String LOG_TAG = ServerAuthenticate.class.getSimpleName();
    private static ServerAuthenticate instance;
    private static final Map<String, User> users = User.createDummyUsers(5);

    public static ServerAuthenticate getInstance() {
        if (instance == null) {
            instance = new ServerAuthenticate();
        }
        return instance;
    }

    String userSignIn(String userName, String userPass, String mAuthTokenType) {
        EvaApiService api = ServiceGenerator.createService(EvaApiService.class);
        Call<TokenResponse> call = api.getToken(new com.groep11.eva_app.data.remote.User(userName, userPass));

        Response<TokenResponse> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        if (response == null || !response.isSuccess()) {
            Log.e(LOG_TAG, "signin call to api didn't work, response == null or !isSuccess : response=" + response);
            return null;
        }
        TokenResponse tokenResponse = response.body();
        if (tokenResponse == null) {
            Log.e(LOG_TAG, "TokenResponse is null");
            return null;
        }

        String token = tokenResponse.getToken();
        Log.v(LOG_TAG, "TokenResponse: " + tokenResponse);

        if(TextUtils.isEmpty(token)){
            Log.w(LOG_TAG, "Token from server is empty");
            return null;
        }

        return token;
    }

    String userSignUp(String userName, String userPass, String mAuthTokenType) {
        User user = new User(userName, userPass, "token" + users.size());
        Log.d(LOG_TAG, String.format("User signup: name(%s), pass(%s), token(%s)", user.name, user.password, user.token));
        users.put(user.name, user);
        return user.token;
    }

    private static class User {
        public final String name;
        public final String password;
        public final String token;

        User(String name, String password, String token) {
            this.name = name;
            this.password = password;
            this.token = token;
        }

        private static Map<String, User> createDummyUsers(int amount) {
            Map<String, User> users = new HashMap<>();
            for (int i = 0; i < amount; i++) {
                User user = new User("user" + i, "pass" + i, "token" + i);
                users.put(user.name, user);
            }
            return users;
        }
    }
}
