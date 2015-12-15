package com.groep11.eva_app.ui.fragment;


import android.accounts.Account;
import android.app.Fragment;
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

public class RegistrationFragment extends Fragment implements SignInFinishedCallback {
    public static final String TAG = "REGISTRATION";

    @Bind(R.id.input_registration_mail)
    EditText mInputMail;
    @Bind(R.id.input_registration_password)
    EditText mInputPassword;


    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance() {
        RegistrationFragment fragment = new RegistrationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        // Non-activity binding for butterknife
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.btn_registration_signup)
    public void showMain() {
        Log.i(TAG, "Signup clicked!");
        String username = mInputMail.getText().toString();
        String password = mInputPassword.getText().toString();
        boolean createNewAccount = true;
        AccountGeneral.submit(this.getActivity().getApplicationContext(), username, password, createNewAccount, this);
    }

    @OnClick(R.id.text_registration_login)
    public void showLogin() {
        Log.i(TAG, "Login clicked!");
        getFragmentManager().popBackStack();
    }

    @Override
    public void signInFinished(Account account) {
        // login failed
        if (account == null) {
            // TODO: cleanup with resources etc
            Toast.makeText(this.getActivity().getApplicationContext(), "Failed to register, do you have an internet connection?", Toast.LENGTH_LONG).show();
            return;
        }
        // login succeeded
        Toast.makeText(this.getActivity().getApplicationContext(), "Registration succeeded", Toast.LENGTH_SHORT).show();
        // TODO: Show difficulty screen
        this.getActivity().finish();
    }
}
