package com.jrsoftware.websoap.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteList;

/**
 * Created by jriley on 1/26/16.
 * ListView adapter for Site Entries
 */
public class SiteListAdapter extends ArrayAdapter<SiteEntry> {

    private SiteList bookmarks;

    public SiteListAdapter(Context context){
        super(context, R.layout.list_item_bookmark);
    }

    public SiteListAdapter(Context context, SiteList bookmarks){
        this(context);
        this.bookmarks = bookmarks;
    }

    @Override
    public int getCount() {
        if(bookmarks == null)
            return 0;
        else
            return bookmarks.size();
    }

    @Override
    public SiteEntry getItem(int position) {
        if(bookmarks == null || bookmarks.size() < 1 || bookmarks.size() <= position)
            return null;
        else
            return bookmarks.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Context context = getContext();

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_item_bookmark, null);
        }

        SiteEntry entry = getItem(position);

        if (entry != null) {
            TextView title = (TextView) view.findViewById(R.id.text_title);
            TextView url = (TextView) view.findViewById(R.id.text_url);

            if(title != null)
                title.setText(entry.title());

            if(url != null)
                url.setText(entry.url());
        }

        return view;
    }
}
