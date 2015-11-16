package com.groep11.eva_app.ui.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groep11.eva_app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChallengeCompleteDialog extends DialogFragment {

    // Empty constructor is required for DialogFragment
    public ChallengeCompleteDialog() {}

    public static ChallengeCompleteDialog newInstance(String title) {
        ChallengeCompleteDialog dialog = new ChallengeCompleteDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_challenge_complete, container, false);
        // Non-activity binding for butterknife
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title (+ default value)
        String title = getArguments().getString("title", "Congratulations!");
        getDialog().setTitle(title);
    }

    @OnClick(R.id.dialog_btn_ok)
    public void onOk(View view) {
        this.dismiss();
    }
}
