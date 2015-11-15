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

public class MainActivity extends HiddenBarActivity implements ShowChallengeFragment.OnItemClickListener{
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
