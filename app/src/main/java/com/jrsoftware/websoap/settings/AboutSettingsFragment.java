package com.jrsoftware.websoap.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.MenuItem;

import com.jrsoftware.websoap.R;

public class AboutSettingsFragment extends CustomPreferenceFragment {

    private static final String LOG_TAG = "ABOUT-PREFS";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);
        setHasOptionsMenu(true);

        AppCompatPreferenceActivity activity = getPreferenceActivity();
        if(activity != null){
            PackageInfo info = activity.getPackageInfo();
            if(info != null){
                Preference versionName = findPreference(getString(R.string.pref_key_version_name));
                Preference versionCode = findPreference(getString(R.string.pref_key_version_code));

                bindPreference(versionName);
                bindPreference(versionCode);

                versionName.setSummary(info.versionName);
                versionCode.setSummary(Integer.toString(info.versionCode));
            }
            else{
                Log.w(LOG_TAG, "Package Info Null");
            }
        }

        Preference licensePref = findPreference(getString(R.string.pref_key_license));
        if(licensePref != null) {
            licensePref.setOnPreferenceClickListener(licenseClick);
            bindPreference(licensePref);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public AppCompatPreferenceActivity getPreferenceActivity(){
        Activity activity = getActivity();
        if(activity instanceof AppCompatPreferenceActivity)
            return (AppCompatPreferenceActivity)activity;
        else
            return null;
    }

    private Preference.OnPreferenceClickListener licenseClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AppCompatPreferenceActivity activity = getPreferenceActivity();
            if(activity != null)
                activity.toLicenseActivity();
            return true;
        }
    };
}
