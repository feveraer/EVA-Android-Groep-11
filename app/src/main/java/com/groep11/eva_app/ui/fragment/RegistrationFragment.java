package com.groep11.eva_app.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.authentication.AccountGeneral;
import com.groep11.eva_app.service.EvaSyncAdapter;
import com.groep11.eva_app.ui.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationFragment extends Fragment {
    public static final String TAG = "REGISTRATION";

    @Bind(R.id.input_registration_mail) EditText mInputMail;
    @Bind(R.id.input_registration_password) EditText mInputPassword;


    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance() {
        RegistrationFragment fragment = new RegistrationFragment();
        return  fragment;
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
        // TODO: Post registration
        // TODO: Show difficulty screen

        String username = mInputMail.getText().toString();
        String password = mInputPassword.getText().toString();

        EvaSyncAdapter.createAccount(getActivity().getApplicationContext(), username, password);

        // true -> force new account
        AccountGeneral.submit(getActivity().getApplicationContext(), username, password, true);

        getActivity().finish();
    }

    @OnClick(R.id.text_registration_login)
    public void showLogin(){
        Log.i(TAG, "Login clicked!");
        getFragmentManager().popBackStack();
    }


}
