package com.groep11.eva_app.ui.fragment;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.groep11.eva_app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowProgressFragment extends Fragment {
    public static final String TAG = "PROGRESS";
    private static final String PROGRESS_PREFIX = "Dag";
    private static final int ANIMATION_DELAY = 100;
    private static String ANIMATION_ARRAY_TYPE = "array",
                          ANIMATION_ARRAY_NAME = "completed_day_";

    private int progressCounter = 0;

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

        //TODO: animation if progressCounter not 0
        progressImage.setBackground(getLastAnimationFrame(this.getActivity()));
        //String.format needed, setText with an integer argument looks for a resource
        progressText.setText(String.format("%s %d", PROGRESS_PREFIX, progressCounter + 1));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        progressImage.setBackground(getLastAnimationFrame(this.getActivity()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("progressCounter", progressCounter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null){
            progressCounter = savedInstanceState.getInt("progressCounter");
            progressText.setText(String.format("%s %d", PROGRESS_PREFIX, progressCounter + 1));
        }

    }

    public void clearProgression(){
        progressCounter = 0;
        progressText.setText(String.format("%s %d", PROGRESS_PREFIX, progressCounter + 1));
        progressImage.setBackgroundResource(R.drawable.tree_frame_01);
    }

    public void increaseProgress(){
        if(progressCounter < 21) {
            progressCounter++;
            progressText.setText(progressCounter == 21 ?
                    "Alle voltooid!" :
                    String.format("%s %d", PROGRESS_PREFIX, progressCounter + 1));
        }

        AnimationDrawable progressAnimation = createAnimationFromXMLArray(this.getActivity(), false);
        progressImage.setBackground(progressAnimation);
        progressAnimation.start();
    }

    @OnClick(R.id.fragment_show_progress_container)
    public void onFragmentClick(){
        increaseProgress();
    }

    /**
     * Creates an AnimationDrawable from the tree_animation_array.xml file based on the progressCounter
     * @param fromStart starts animation from the first day when true
     */
    private AnimationDrawable createAnimationFromXMLArray(Context context, boolean fromStart) {
        AnimationDrawable animation = new AnimationDrawable();
        //Start animation from the first day when fromStart is true
        int startDay = fromStart ? 1 : progressCounter;

        for(int dayIndex = startDay; dayIndex <= progressCounter; dayIndex++){
            //Find the array with the animation frames for dayIndex
            TypedArray array = context.getResources().obtainTypedArray(getArrayIdFromDay(dayIndex));

            //Add every frame from the array to our animation
            for (int frameIndex = 0; frameIndex < array.length(); frameIndex++) {
                animation.addFrame(array.getDrawable(frameIndex), ANIMATION_DELAY);
            }

            //Make the allocated memory from our TypedArray available immediately
            array.recycle();
        }

        //Turn off the animation loop
        animation.setOneShot(true);
        return animation;
    }

    private Drawable getLastAnimationFrame(Context context) {
        //If the user hasn't made any progress yet, show him the first frame
        if(progressCounter == 0) return ContextCompat.getDrawable(context, R.drawable.tree_frame_01);

        TypedArray array = context.getResources().obtainTypedArray(getArrayIdFromDay(progressCounter));
        Drawable lastFrame = array.getDrawable(array.length() - 1);
        array.recycle();

        return lastFrame;
    }

    //Find the array id based on dayIndex: "completed_day_:dayIndex"
    private int getArrayIdFromDay(int dayIndex){
        return getResources().getIdentifier(ANIMATION_ARRAY_NAME + dayIndex, ANIMATION_ARRAY_TYPE, this.getActivity().getPackageName());
    }

}
