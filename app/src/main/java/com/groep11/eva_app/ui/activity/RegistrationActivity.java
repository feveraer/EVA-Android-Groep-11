package com.groep11.eva_app.ui.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.groep11.eva_app.R;
import com.groep11.eva_app.RegistrationFragment;
import com.groep11.eva_app.ui.fragment.CategoryFragment;

import butterknife.Bind;

public class RegistrationActivity extends TransparentBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Make our action bar transparent for future use
        initActionBar();

        // Hide action bar on Category fragment
        getActionBar().hide();

        // Add Registration fragment to our fragment container
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        RegistrationFragment registrationFragment = RegistrationFragment.newInstance();

        fragmentTransaction.add(R.id.fragment_registration_container, registrationFragment, CategoryFragment.TAG);
        fragmentTransaction.commit();
    }
}
