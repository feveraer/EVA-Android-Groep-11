package com.groep11.eva_app.ui.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.groep11.eva_app.R;
import com.groep11.eva_app.service.EvaSyncAdapter;
import com.groep11.eva_app.ui.fragment.CategoryFragment;
import com.groep11.eva_app.ui.fragment.ChallengeCompleteDialog;
import com.groep11.eva_app.ui.fragment.ShowProgressFragment;
import com.groep11.eva_app.ui.fragment.interfaces.IOnFocusListenable;
import com.groep11.eva_app.util.DateFaker;

public class MainActivity extends TransparentBarActivity implements ChallengeCompleteDialog.OnItemClickListener{
    public static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make our action bar transparent for future use
        initActionBar();

        // Hide action bar on Category fragment
        getActionBar().hide();

        // Add Category fragment to our fragment container
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        CategoryFragment categoryFragment = CategoryFragment.newInstance();

        fragmentTransaction.add(R.id.fragment_main_container, categoryFragment, CategoryFragment.TAG);
        fragmentTransaction.commit();

        setOnBackStackChangedListener();

        EvaSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        CategoryFragment categoryFragment = (CategoryFragment) getFragmentManager()
                .findFragmentByTag(CategoryFragment.TAG);

        if( categoryFragment != null &&
            categoryFragment instanceof IOnFocusListenable ) {
            ((IOnFocusListenable) categoryFragment).onWindowFocusChanged(hasFocus);
        }
    }

    @Override
    public void onComplete() {
        ShowProgressFragment progressFragment = (ShowProgressFragment) getFragmentManager()
                .findFragmentByTag(ShowProgressFragment.TAG);
        if (progressFragment != null) {
            progressFragment.increaseProgress();
            new DateFaker(this).nextDay();
        }
    }
}
