package com.groep11.eva_app.ui.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.groep11.eva_app.R;
import com.groep11.eva_app.ui.fragment.ShowProgressFragment;

public abstract class TransparentBarActivity extends Activity{

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

    protected void initActionBar(){
        // Set color of back button
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.light_green_500), PorterDuff.Mode.SRC_ATOP);
        getActionBar().setHomeAsUpIndicator(upArrow);

        // Create a transparent color drawable
        ColorDrawable newColor = new ColorDrawable(getResources().getColor(R.color.light_green_200));
        //newColor.setAlpha(256);

        // Use this transparent color drawable to make our action bar transparent
        getActionBar().setBackgroundDrawable(newColor);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setElevation(0);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.statusBarColor));
    }

    // Toggles our back button in our ActionBar depending on the backstack count
    protected void setOnBackStackChangedListener(){
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
}
