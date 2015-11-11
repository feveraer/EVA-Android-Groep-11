package com.groep11.eva_app.ui.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.groep11.eva_app.R;
import com.groep11.eva_app.data.remote.Category;
import com.groep11.eva_app.service.EvaSyncAdapter;
import com.groep11.eva_app.ui.fragment.CategoryFragment;
import com.groep11.eva_app.ui.fragment.ShowChallengeFragment;
import com.groep11.eva_app.ui.fragment.ShowProgressFragment;
import com.groep11.eva_app.util.DateFaker;

public class MainActivity extends Activity implements ShowChallengeFragment.OnItemClickListener{

    public static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionBar();

        // Hide action bar on Category fragment
        getActionBar().hide();

        // Add Category fragment to our fragment container
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        CategoryFragment categoryFragment = CategoryFragment.newInstance();

        fragmentTransaction.add(R.id.fragment_container, categoryFragment, CategoryFragment.TAG);
        fragmentTransaction.commit();

        setOnBackStackChangedListener();

        EvaSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                // When the back button is pressed,
                // rewind the last transaction from the backstack
                getFragmentManager().popBackStack();
                // Hide the action bar when returning from challengeDetails
                getActionBar().hide();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_clear_progression:
                ShowProgressFragment fragment = (ShowProgressFragment) getFragmentManager().findFragmentByTag(ShowProgressFragment.TAG);
                fragment.clearProgression();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Toggles our back button in our ActionBar depending on the backstack count
    private void setOnBackStackChangedListener(){
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int stackHeight = getFragmentManager().getBackStackEntryCount();
                // Another fragment is on the backstack, enable back button
                if (stackHeight > 0) {
                    getActionBar().setHomeButtonEnabled(true);
                    getActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getActionBar().setDisplayHomeAsUpEnabled(false);
                    getActionBar().setHomeButtonEnabled(false);
                }
            }
        });
    }

    // Initialize the properties of our ActionBar:
    // Transparent, no title & green back button
    private void initActionBar(){
        // Set color of back button
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.light_green_500), PorterDuff.Mode.SRC_ATOP);
        getActionBar().setHomeAsUpIndicator(upArrow);

        // Create a transparent color drawable
        ColorDrawable newColor = new ColorDrawable(getResources().getColor(R.color.white_1000));
        newColor.setAlpha(256);

        // Use this transparent color drawable to make our action bar transparent
        getActionBar().setBackgroundDrawable(newColor);
        getActionBar().setDisplayShowTitleEnabled(false);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.statusBarColor));
    }


    @Override
    public void onComplete() {
        ShowChallengeFragment challengeFragment = (ShowChallengeFragment) getFragmentManager()
                .findFragmentByTag(ShowChallengeFragment.TAG);
        ShowProgressFragment progressFragment = (ShowProgressFragment) getFragmentManager()
                .findFragmentByTag(ShowProgressFragment.TAG);
        if (challengeFragment != null && progressFragment != null) {
            progressFragment.increaseProgress();
            new DateFaker(this).nextDay();
        }
    }
}
