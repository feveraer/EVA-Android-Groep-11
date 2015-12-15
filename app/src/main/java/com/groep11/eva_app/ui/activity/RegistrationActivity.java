package com.groep11.eva_app.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.groep11.eva_app.R;
import com.groep11.eva_app.ui.fragment.LoginFragment;
import com.groep11.eva_app.ui.fragment.CategoryFragment;
import com.groep11.eva_app.util.DateFaker;

public class RegistrationActivity extends TransparentBarActivity {
    public static final String ARG_ACCOUNT_TYPE = "argAuthType";
    public static final String ARG_AUTH_TYPE = "argAuthType";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "argIsAddingNewAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Make our action bar transparent for future use
        initActionBar();

        // Hide action bar on Category fragment
        getActionBar().hide();

        // Add Registration fragment to our fragment container
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        LoginFragment loginFragment = LoginFragment.newInstance();

        fragmentTransaction.add(R.id.fragment_registration_container, loginFragment, CategoryFragment.TAG);
        fragmentTransaction.commit();

        setOnBackStackChangedListener();
        new DateFaker(this).nextDay();
    }
}
