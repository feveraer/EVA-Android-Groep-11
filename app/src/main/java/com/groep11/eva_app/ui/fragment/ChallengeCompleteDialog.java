package com.groep11.eva_app.ui.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groep11.eva_app.R;


public class ChallengeCompleteDialog extends DialogFragment {

    // Empty constructor is required for DialogFragment
    public ChallengeCompleteDialog() {}

    public static ChallengeCompleteDialog newInstance(String arg) {
        ChallengeCompleteDialog dialog = new ChallengeCompleteDialog();
        Bundle args = new Bundle();
        args.putString("arg", arg);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenge_complete, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
