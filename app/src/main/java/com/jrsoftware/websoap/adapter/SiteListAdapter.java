package com.jrsoftware.websoap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteList;
import com.jrsoftware.websoap.util.AppUtils;

import java.util.ArrayList;

/**
 * Created by jriley on 1/26/16.
 * ListView adapter for Site Entries
 */
public class SiteListAdapter extends ArrayAdapter<SiteEntry> {

    private ArrayList<SiteEntry> entries;

    public SiteListAdapter(Context context){
        super(context, R.layout.list_item_bookmark);
    }

    public SiteListAdapter(Context context, ArrayList<SiteEntry> entries){
        this(context);
        this.entries = entries;
    }

    @Override
    public int getCount() {
        if(entries == null)
            return 0;
        else
            return entries.size();
    }

    @Override
    public SiteEntry getItem(int position) {
        if(entries == null || entries.size() < 1 || entries.size() <= position)
            return null;
        else
            return entries.get(position);
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
                title.setText(AppUtils.concatWithEllipsis(entry.title(), 48));

            if(url != null)
                url.setText(AppUtils.concatWithEllipsis(entry.url(), 48));
        }

        return view;
    }
}
