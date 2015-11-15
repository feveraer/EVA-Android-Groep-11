package com.groep11.eva_app.ui.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.groep11.eva_app.R;

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

        // Show the action bar for navigation
        getActivity().getActionBar().show();

        return rootView;
    }

    @OnClick(R.id.btn_registration_signup)
    public void showMain() {
        Log.i(TAG, "Login clicked!");
        // TODO: Authenticate login
        // TODO: Login succeeded    --> Show Main Activity (category or main fragment depending on challenge completion)
        // TODO: Login failed       --> Show toast with login error message
    }


}
