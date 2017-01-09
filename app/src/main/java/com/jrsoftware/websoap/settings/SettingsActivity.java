package com.jrsoftware.websoap.settings;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.activity.MainActivity;
import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.util.AppUtils;

import java.util.List;

/**
 * An abstract {@link PreferenceActivity} for presenting a set of application settings.
 */
public class SettingsActivity extends AppCompatPreferenceActivity
        implements CustomPreferenceFragment.PreferenceUpdateListener {

    public static final String ARG_SITE = "com.jrsoftware.websoap.site";

    private SiteEntry site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        Intent i = getIntent();
        if(i != null){
            Bundle extras = i.getExtras();
            if(extras != null)
                site = (SiteEntry) extras.get(ARG_SITE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*
        if (id == android.R.id.home) {
            backToMainMenu();
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return AppUtils.isXLargeTablet(this);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager.getBackStackEntryCount() < 1) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(MainActivity.ARG_SITE, (Parcelable) site);
            startActivity(i);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public void bindPreference(Preference preference) {
        //Initialize the Preference Change Listener
        preference.setOnPreferenceChangeListener(this);

        // Trigger the event immediately with the preference's
        // current value.
        onPreferenceChange(preference, AppUtils.getPreferences(this)
                                                .getString(preference.getKey(), ""));
    }

    @Override
    public void bindBooleanPreference(Preference preference) {
        //Initialize the Preference Change Listener
        preference.setOnPreferenceChangeListener(this);

        // Trigger the event immediately with the preference's
        // current value.
        onPreferenceChange(preference, AppUtils.getPreferences(this)
                                        .getBoolean(preference.getKey(), false));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        }
        else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }

        return true;
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralSettingsFragment.class.getName().equals(fragmentName)
                || DataSettingsFragment.class.getName().equals(fragmentName)
                || NetworkSettingsFragment.class.getName().equals(fragmentName)
                || AboutSettingsFragment.class.getName().equals(fragmentName);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
