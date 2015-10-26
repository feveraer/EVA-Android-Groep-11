package com.groep11.eva_app.ui.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.groep11.eva_app.R;
import com.groep11.eva_app.service.EvaSyncAdapter;
import com.groep11.eva_app.ui.fragment.ShowChallengeFragment;
import com.groep11.eva_app.ui.fragment.ShowProgressFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Add challenge fragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        ShowProgressFragment progressFragment = ShowProgressFragment.newInstance();
        ShowChallengeFragment challengeFragment = ShowChallengeFragment.newInstance();

        fragmentTransaction.add(R.id.fragment_container, progressFragment, ShowProgressFragment.TAG);
        fragmentTransaction.add(R.id.fragment_container, challengeFragment, ShowChallengeFragment.TAG);
        fragmentTransaction.commit();

        setOnBackStackChangedListener();

        EvaSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
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
