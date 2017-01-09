package com.jrsoftware.websoap.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.adapter.SiteListAdapter;
import com.jrsoftware.websoap.controller.HistoryManager;
import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteTree;
import com.jrsoftware.websoap.util.DialogUtils;

import java.util.ArrayList;

public class BookmarkListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
                                                                    AdapterView.OnItemLongClickListener {

    public static final String ARG_SITE = "com.jrsoftware.websoap.site";
    public static final String ARG_BOOKMARKS = "com.jrsoftware.websoap.bookmarks";
    public static final String ARG_HISTORY = "com.jrsoftware.websoap.history";

    ListView listView;

    private ArrayList<SiteEntry> bookmarks;
    private HistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);
        setTitle(R.string.title_bookmarks);

        listView = (ListView) findViewById(R.id.list_bookmarks);

        Intent i = getIntent();
        if(i != null){
            Bundle extras = i.getExtras();
            if(extras != null) {
                SiteTree bookmarkTree = extras.getParcelable(ARG_BOOKMARKS);
                historyManager = extras.getParcelable(ARG_HISTORY);

                if(bookmarkTree != null)
                    bookmarks = bookmarkTree.asArrayListReversed();
            }
        }

        setAdapter(bookmarks);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.ARG_BOOKMARKS, (Parcelable) new SiteTree(bookmarks));
        i.putExtra(MainActivity.ARG_HISTORY, historyManager);
        startActivity(i);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
        SiteEntry entry = bookmarks.get(position);

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.ARG_HISTORY, historyManager);
        i.putExtra(MainActivity.ARG_SITE, (Parcelable) entry);
        i.putExtra(MainActivity.ARG_BOOKMARKS, (Parcelable) new SiteTree(bookmarks));

        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long rowId) {
        openBookmarkDialog(position);
        return true;
    }

    private void setAdapter(ArrayList<SiteEntry> list){
        SiteListAdapter adapter = new SiteListAdapter(this, bookmarks);
        listView.setAdapter(adapter);
    }

    /**
     * Opens the options dialog for long clicking a SiteEntry object
     * @param position - index of the object in question
     */
    void openBookmarkDialog(final int position){
        final Context context = this;
        DialogUtils.getTextChoiceDialog(this,
                R.array.array_bookmark_options, R.string.text_bookmark,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface parentDialog, int which) {
                        switch (which) {
                            case 0:         //Delete SiteEntry
                                DialogUtils.getConfirmationDialog(
                                        context, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface childDialog, int which) {
                                                bookmarks.remove(position);
                                                setAdapter(bookmarks);

                                                childDialog.dismiss();
                                                parentDialog.dismiss();
                                            }
                                        }).show();
                                break;
                        }
                    }
                }
        ).show();
    }
}
