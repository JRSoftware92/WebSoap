package com.jrsoftware.websoap.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.adapter.SiteListAdapter;
import com.jrsoftware.websoap.controller.HistoryManager;
import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteList;
import com.jrsoftware.websoap.util.DialogUtils;

import java.util.Collections;

public class WebHistoryActivity extends AppCompatActivity {

    public static final String ARG_HISTORY = "com.jrsoftware.websoap.history";
    private static final String LOG_TAG = "WEB-HISTORY";

    ListView listView;

    private SiteList history;
    private HistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_history);
        setTitle(R.string.title_web_history);

        final Context context = this;

        listView = (ListView) findViewById(R.id.list_history);

        Intent i = getIntent();
        if(i != null){
            Bundle extras = i.getExtras();
            if(extras != null)
                historyManager = extras.getParcelable(ARG_HISTORY);

            if(historyManager != null)
                history = historyManager.getSiteHistory();

            if(history != null)
                Collections.reverse(history);
        }
        SiteListAdapter adapter = new SiteListAdapter(this, history);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SiteEntry entry = history.get(position);
                Collections.reverse(history);
                historyManager.setSiteHistory(history);

                Log.i(LOG_TAG, String.format("Chosen Entry: %s", entry.title()));
                Log.i(LOG_TAG, String.format("Size of history: %d", history.size()));

                Intent i = new Intent(context, MainActivity.class);
                i.putExtra(MainActivity.ARG_SITE, (Parcelable)entry);
                i.putExtra(MainActivity.ARG_HISTORY, historyManager);

                startActivity(i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openHistoryDialog(position);
                return false;
            }
        });
    }

    /**
     * Opens the options dialog for long clicking a SiteEntry object
     * @param position - index of the object in question
     */
    void openHistoryDialog(final int position){
        final Context context = this;
        DialogUtils.getTextChoiceDialog(this,
                R.array.array_history_options, R.string.title_web_history,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface parentDialog, int which) {
                        switch (which) {
                            case 2:         //Delete SiteEntry
                                DialogUtils.showConfirmationDialog(
                                        context, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface childDialog, int which) {
                                                history.remove(position);

                                                childDialog.dismiss();
                                                parentDialog.dismiss();
                                            }
                                        });
                                break;
                        }
                    }
                }
        );
    }

    //TODO - Override onBackPressed --> Resend intent with updated manager
}
