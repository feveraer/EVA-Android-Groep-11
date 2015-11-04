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

public class MainActivity extends Activity {

    public static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionBar();

        // Hide action bar on categoryFragment
        getActionBar().hide();

        //Add challenge fragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        CategoryFragment categoryFragment = CategoryFragment.newInstance();
//        ShowProgressFragment progressFragment = ShowProgressFragment.newInstance();
//        ShowChallengeFragment challengeFragment = ShowChallengeFragment.newInstance();

        fragmentTransaction.add(R.id.fragment_container, categoryFragment, CategoryFragment.TAG);
//        fragmentTransaction.add(R.id.fragment_container, progressFragment, ShowProgressFragment.TAG);
//        fragmentTransaction.add(R.id.fragment_container, challengeFragment, ShowChallengeFragment.TAG);
        fragmentTransaction.commit();

        setOnBackStackChangedListener();

        EvaSyncAdapter.initializeSyncAdapter(this);
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                getFragmentManager().popBackStack();
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

    private void setOnBackStackChangedListener(){
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int stackHeight = getFragmentManager().getBackStackEntryCount();
                if (stackHeight > 0) { // if we have something on the stack (doesn't include the current shown fragment)
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
