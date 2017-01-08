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
import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteList;
import com.jrsoftware.websoap.util.DialogUtils;

public class BookmarkListActivity extends AppCompatActivity {

    public static final String ARG_BOOKMARKS = "com.jrsoftware.websoap.bookmarks";

    ListView listView;

    private SiteList bookmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);
        setTitle(R.string.title_bookmarks);

        final Context context = this;

        listView = (ListView) findViewById(R.id.list_bookmarks);

        Intent i = getIntent();
        if(i != null){
            Bundle extras = i.getExtras();
            if(extras != null)
                bookmarks = extras.getParcelable(ARG_BOOKMARKS);
        }
        SiteListAdapter adapter = new SiteListAdapter(this, bookmarks);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SiteEntry entry = bookmarks.get(position);

                Intent i = new Intent(context, MainActivity.class);
                i.putExtra(MainActivity.ARG_SITE, (Parcelable)entry);
                i.putExtra(MainActivity.ARG_BOOKMARKS, (Parcelable)bookmarks);

                startActivity(i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openBookmarkDialog(position);
                return false;
            }
        });
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
                            case 2:         //Delete SiteEntry
                                DialogUtils.showConfirmationDialog(
                                        context, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface childDialog, int which) {
                                                bookmarks.remove(position);

                                                //FIXME - Does not save without bookmark selected

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
}
