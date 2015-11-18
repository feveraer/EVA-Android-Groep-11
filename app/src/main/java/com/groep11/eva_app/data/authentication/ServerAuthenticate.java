package com.groep11.eva_app.data.authentication;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

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
        User user = users.get(userName);
        if (user != null) {
            Log.d(LOG_TAG, String.format("User signin succeeded: name(%s), pass(%s), token(%s)", user.name, user.password, user.token));
        } else {
            Log.d(LOG_TAG, String.format("User signin FAILED: name(%s), pass(%s), tokenType(%s)", userName, userPass, mAuthTokenType));

        }
        return user != null && user.password.equals(userPass) ? user.token : null;
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
