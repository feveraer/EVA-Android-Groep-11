package com.groep11.eva_app.ui.activity;

import android.accounts.AccountManager;
import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.MediaController;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.VideoView;

import com.groep11.eva_app.R;
import com.groep11.eva_app.ui.fragment.LoginFragment;
import com.groep11.eva_app.ui.fragment.CategoryFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegistrationActivity extends TransparentBarActivity {
    public static final String ARG_ACCOUNT_TYPE = "argAuthType";
    public static final String ARG_AUTH_TYPE = "argAuthType";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "argIsAddingNewAccount";

    @Bind(R.id.video_registration)
    VideoView mVideo;

    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.bind(this);

        // Make our action bar transparent for future use
        initActionBar();

        // Hide action bar on Category fragment
        getActionBar().hide();

        // Add Registration fragment to our fragment container
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        LoginFragment loginFragment = LoginFragment.newInstance();

        fragmentTransaction.add(R.id.fragment_registration_container, loginFragment, CategoryFragment.TAG);
        fragmentTransaction.commit();

        setOnBackStackChangedListener();

        startBackgroundVideo();
    }

    private void startBackgroundVideo(){
        // Get our video's path set it as the VideoView's resource
        int videoId = getResources().getIdentifier("vegimovie", "raw", getPackageName());
        String videoPath = "android.resource://" + getPackageName() + "/" + videoId;

        mVideo.setVideoURI(Uri.parse(videoPath));

        // Make the video loop
        mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        // Start the video
        mVideo.start();
    }
}
