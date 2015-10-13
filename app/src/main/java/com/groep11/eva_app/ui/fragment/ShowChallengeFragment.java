package com.groep11.eva_app.ui.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.groep11.eva_app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowChallengeFragment extends Fragment {
    private TextView mIdView;
    private TextView mTitleView;
    private TextView mDifficultyView;

    public ShowChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_challenge, container, false);
        mIdView = (TextView) rootView.findViewById(R.id.text_challenge_id);
        mTitleView = (TextView) rootView.findViewById(R.id.text_challenge_title);
        mDifficultyView = (TextView) rootView.findViewById(R.id.text_challenge_difficulty);
        return rootView;
    }


}
