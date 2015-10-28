package com.groep11.eva_app.ui.fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.groep11.eva_app.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowProgressFragment extends Fragment {
    public static final String TAG = "PROGRESS";
    private static final String TREE_FRAME = "tree_frame_";
    private int progressCounter = 0;
    private List<Integer> animationFrames;

    @Bind(R.id.image_progress)
    ImageView progressImage;
    @Bind(R.id.text_progress)
    TextView progressText;

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

        progressImage.setBackgroundResource(R.drawable.tree_frame_01);
        //String.format needed, setText with an integer argument looks for a resource
        progressText.setText(String.format("%d", progressCounter));
        animationFrames = new ArrayList<>();

        return view;
    }

    public void clearProgression(){
        progressCounter = 0;
        progressText.setText(String.format("%d", progressCounter));
        animationFrames.clear();
        progressImage.setBackgroundResource(R.drawable.tree_frame_01);
    }

    public void increaseProgress(){
        if(progressCounter < 21) {
            progressCounter++;
            progressText.setText(String.format("%d", progressCounter));
        }

        //Append more animations depending on progressCounter
        adjustAnimationFrames();
        //Set the progressImage background
        progressImage.setBackground(createFrom(getActivity(), animationFrames, 50));

        //Get the drawable animation and start it
        AnimationDrawable progressAnimation = (AnimationDrawable) progressImage.getBackground();
        //Only run once
        progressAnimation.setOneShot(true);
        progressAnimation.start();
    }

    @OnClick(R.id.fragment_show_progress_container)
    public void onFragmentClick(){
        increaseProgress();
    }

    /**
     * Helper function to create the drawable animation at runtime
     * @param context
     * @param drawableIds
     * @param duration
     * @return
     */
    private AnimationDrawable createFrom(Context context, List<Integer> drawableIds, int duration) {
        AnimationDrawable ad = new AnimationDrawable();
        for (int id : drawableIds) {
            ad.addFrame(context.getResources().getDrawable(id, null), duration);
        }
        return ad;
    }

    /**
     * Helper function to append animation frames with each progression step
     */
    private void adjustAnimationFrames() {
        //6 frames each for the first 2 animations
        if (progressCounter == 1 || progressCounter == 2) {
            for(int i = 0; i < 6; i++) {
                int treeFrameBaseId = (progressCounter - 1) * 6;
                animationFrames.add(getResources().getIdentifier(
                        treeFrameBaseId + i + 1 < 10 ?
                        TREE_FRAME + "0" + (treeFrameBaseId + i + 1) :
                        TREE_FRAME + (treeFrameBaseId + i + 1),
                        "drawable", getActivity().getPackageName()));
            }
        } else {
            //only 1 frame added for the other progression steps
            animationFrames.add(getResources().getIdentifier(
                    TREE_FRAME + (progressCounter + 10),
                    "drawable", getActivity().getPackageName()));
        }
    }
}
