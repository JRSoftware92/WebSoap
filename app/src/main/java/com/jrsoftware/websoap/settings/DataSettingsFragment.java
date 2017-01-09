package com.jrsoftware.websoap.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.controller.AppDataCenter;
import com.jrsoftware.websoap.util.AppUtils;
import com.jrsoftware.websoap.util.DialogUtils;

import java.io.IOException;

/**
 * This fragment shows data preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 *
 * TODO - Need to update MainActivity appropriately
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DataSettingsFragment extends CustomPreferenceFragment {
    private static final String LOG_TAG = "GEN-PREF-FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_data);
        setHasOptionsMenu(true);

        Preference clearHistoryData = findPreference(getString(R.string.pref_key_clear_selected_history));
        clearHistoryData.setOnPreferenceClickListener(clearHistoryDataClick);

        bindPreference(findPreference(getString(R.string.pref_key_history_time_period)));
        bindBooleanPreference(findPreference(getString(R.string.pref_key_remember_history)));
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

    private Preference.OnPreferenceClickListener clearHistoryDataClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog dialog = DialogUtils.getMessageDialog(getActivity(),
                    R.string.title_dialog_confirmation,
                    R.string.message_dialog_cannot_be_undone,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Context context = getActivity();
                            AppDataCenter dataCenter = new AppDataCenter(context);
                            SharedPreferences pref = AppUtils.getPreferences(context);
                            String strVal = pref.getString(getString(R.string.pref_key_history_time_period), "1d");

                            try {
                                dataCenter.loadHistory();
                                dataCenter.clearHistory(strVal);

                                AppUtils.showToastShort(context, "History Successfully Cleared!");
                            }
                            catch(Exception e){
                                AppUtils.showToastLong(context, "Unexpected error encountered while clearing history.");
                                Log.e("DATA-SETTINGS", "Unexpected error encountered while clearing history.");
                                Log.e("DATA-SETTINGS", e.getMessage());
                            }
                            //long checkId = dataCenter.deleteHistory();
                            //AppUtils.showToastLong(getActivity(), String.format("Deleted %d entries", checkId));
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
            return true;
        }
    };
}