package com.groep11.eva_app.ui.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.groep11.eva_app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChallengeCompleteDialog extends DialogFragment {
    private static final String TAG = "COMPLETE";

    @Bind(R.id.dialog_btn_ok) Button mBtnConfirm;
    @Bind(R.id.dialog_btn_share) ImageButton mShareButton;

    private String mCompletedChallenge = "";

    // Empty constructor is required for DialogFragment
    public ChallengeCompleteDialog() {}

    public static ChallengeCompleteDialog newInstance(String title, String completedChallenge) {
        ChallengeCompleteDialog dialog = new ChallengeCompleteDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("completed_challenge", completedChallenge);
        dialog.setArguments(args);
        return dialog;
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onComplete();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;

            if (activity instanceof OnItemClickListener) {
                listener = (OnItemClickListener) activity;
            } else {
                throw new ClassCastException(activity.toString() +
                        " must implement OnItemClickListener");
            }
        }
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
        mCompletedChallenge = getArguments().getString("completed_challenge", "Challenge niet gevonden :(");

        setStyle(DialogFragment.STYLE_NORMAL, R.style.completeDialog);
        getDialog().setTitle(title);
    }

    @OnClick(R.id.dialog_btn_ok)
    public void onConfirm(View view) {
        // Make sure listener is set.
        listener = (OnItemClickListener) getActivity();
        Log.i(TAG, "" + listener);
        if (listener != null) {
            // Increment progress
            listener.onComplete();
        }

        dismiss();
    }

    @OnClick(R.id.dialog_btn_share)
    public void onShare() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, createShareBody());

        startActivity(Intent.createChooser(sharingIntent, "Delen via"));
    }

    private String createShareBody() {
        return String.format("%s\"%s\"%s",
                getString(R.string.complete_share_body_prefix),
                mCompletedChallenge,
                getString(R.string.complete_share_body_postfix));
    }
}
