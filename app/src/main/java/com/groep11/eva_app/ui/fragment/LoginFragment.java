package com.groep11.eva_app.ui.fragment;


import android.accounts.Account;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.authentication.AccountGeneral;
import com.groep11.eva_app.data.authentication.SignInFinishedCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment implements SignInFinishedCallback {
    private static final String TAG = "LOGIN";

    @Bind(R.id.input_login_mail)
    EditText mInputMail;
    @Bind(R.id.input_login_password)
    EditText mInputPassword;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        // Non-activity binding for butterknife
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.text_login_signup)
    public void showRegistration() {
        Log.i(TAG, "Signup clicked!");

        FragmentManager fragmentManager = this.getActivity().getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        RegistrationFragment registrationFragment = RegistrationFragment.newInstance();

        transaction.replace(R.id.fragment_registration_container, registrationFragment, RegistrationFragment.TAG);

        transaction.addToBackStack(null);
        transaction.commit();
    }

    @OnClick(R.id.btn_login_login)
    public void showMain() {
        Log.i(TAG, "Login clicked!");
        String username = mInputMail.getText().toString();
        String password = mInputPassword.getText().toString();
        AccountGeneral.submit(this.getActivity().getApplicationContext(), username, password, AccountGeneral.LOGIN, this);
    }

    @Override
    public void signInFinished(Account account) {
        // login failed
        if (account == null) {
            // TODO: cleanup with resources etc
            Toast.makeText(this.getActivity().getApplicationContext(), "Failed to login, is your email and password correct?", Toast.LENGTH_LONG).show();
            mInputPassword.getText().clear();
            return;
        }
        // login succeeded
        Toast.makeText(this.getActivity().getApplicationContext(), "Login succeeded", Toast.LENGTH_SHORT).show();
        this.getActivity().finish();
    }
}
