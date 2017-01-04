package com.jrsoftware.websoap.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.util.AppUtils;

public class LicenseActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        fragmentManager = getSupportFragmentManager();

        setFragment(LicenseFragment.newInstance());
    }

    private void setFragment(Fragment fragment){
        AppUtils.setFragmentFade(fragmentManager, R.id.frame_license_fragment, fragment);
    }
}
