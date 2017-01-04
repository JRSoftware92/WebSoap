package com.jrsoftware.websoap.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by jriley on 2/9/16.
 * App specific abstract preference fragment
 */
public abstract class CustomPreferenceFragment extends PreferenceFragment{

    private static final String LOG_TAG = "CUSTOM-PREF-FRAGMENT";

    protected PreferenceUpdateListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        Log.v(LOG_TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof PreferenceUpdateListener) {
            setListener((PreferenceUpdateListener) context);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PreferenceUpdateListener");
        }
    }

    @Override
    public void onDetach() {
        Log.v(LOG_TAG, "onDetach");
        super.onDetach();
        setListener(null);
    }

    protected final void setListener(PreferenceUpdateListener listener){
        this.listener = listener;
    }

    protected final void bindPreference(Preference preference){
        if(listener != null)
            listener.bindPreference(preference);
    }

    protected final void bindBooleanPreference(Preference preference){
        if(listener != null)
            listener.bindBooleanPreference(preference);
    }

    public interface PreferenceUpdateListener extends Preference.OnPreferenceChangeListener {
        void bindPreference(Preference preference);
        void bindBooleanPreference(Preference preference);
    }
}
