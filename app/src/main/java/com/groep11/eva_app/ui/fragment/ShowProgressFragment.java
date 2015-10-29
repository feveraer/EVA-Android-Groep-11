package com.groep11.eva_app.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
    private boolean isFragmentRestoration = false;

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
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences.Editor editor = this.getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean("isFragmentRestoration", false);
        editor.apply();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_progress, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "resuming fragment");

        //Find our values in the sharedPreferences
        SharedPreferences prefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        progressCounter = prefs.getInt("progressCounter", 0);
        isFragmentRestoration = prefs.getBoolean("isFragmentRestoration", false);

        //String.format needed, setText with an integer argument looks for a resource

        progressText.setText(progressCounter == 21 ?
                        getResources().getString(R.string.progress_completion_final) :
                        String.format("%s %d", PROGRESS_PREFIX, progressCounter + 1));

        //Only show the full animation when the user has some progress and he's not returning from another fragment
        if(progressCounter == 0 || isFragmentRestoration) {
            progressImage.setBackground(getLastAnimationFrame(this.getActivity()));
        } else {
            AnimationDrawable progressAnimation = createAnimationFromXMLArray(this.getActivity(), true);
            progressImage.setBackground(progressAnimation);
            progressAnimation.start();
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "pausing fragment");

        //Save the progressCounter and isFragmentRestoration values in our sharedPreferences
        SharedPreferences.Editor editor = this.getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt("progressCounter", progressCounter);
        editor.putBoolean("isFragmentRestoration", true);
        editor.apply();

        super.onPause();
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
                    getResources().getString(R.string.progress_completion_final) :
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

        //Get the last frame from our current day array
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
