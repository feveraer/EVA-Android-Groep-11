package com.groep11.eva_app.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.groep11.eva_app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowProgressFragment extends Fragment {
    public static final String TAG = "PROGRESS";
    private int progressCounter = 0;

    @Bind(R.id.image_progress)
    ImageView progressImage;

    public ShowProgressFragment() {
        // Required empty public constructor
    }

    public static ShowProgressFragment newInstance(){
        return new ShowProgressFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_progress, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    public void increaseProgress(){
        if(progressCounter < 21) progressCounter++;
        //TODO: animation, this is a simple test :)
        progressImage.setBackgroundColor(Color.rgb(125, progressCounter * 12, 0));
        Log.i(TAG, "progress..." + progressCounter);
    }
}
