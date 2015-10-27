package com.groep11.eva_app.ui.fragment;


import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public void clearProgression(){
        progressCounter = 0;
        progressImage.setBackgroundColor(Color.rgb(125, 0, 0));
        Log.i(TAG, "progress...cleared");
    }

    public void increaseProgress(){
        if(progressCounter < 21) progressCounter++;

        progressImage.setBackgroundColor(Color.rgb(125, progressCounter * 12, 0));                  //TODO: remove this after animations are implemented
        Log.i(TAG, "progress..." + progressCounter);

        //Swap between two progress animationResources (even/uneven)
        //TODO: use real animation fases
        progressImage.setBackgroundResource((progressCounter % 2 == 0) ? R.drawable.dummy_progress_fase_1 : R.drawable.dummy_progress_fase_2);

        //Get the drawable animation and start it
        AnimationDrawable progressAnimation = (AnimationDrawable) progressImage.getBackground();
        progressAnimation.start();
    }

    @OnClick(R.id.fragment_show_progress_container)
    public void onFragmentClick(){
        increaseProgress();
    }
}
