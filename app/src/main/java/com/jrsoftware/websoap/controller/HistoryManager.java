package com.jrsoftware.websoap.controller;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteTree;
import com.jrsoftware.websoap.util.CustomTimeUtils;

/**
 * Created by jriley on 1/4/17.
 *
 * Data Management Class for managing url history
 */

public class HistoryManager implements Parcelable {
    private static final String LOG_TAG = "HISTORY-MANAGER";
    private SiteEntry current;
    private SiteTree history;

    public HistoryManager(){
        current = null;
        history = new SiteTree();
    }

    public HistoryManager(SiteTree history){
        current = null;
        this.history = history;
    }

    protected HistoryManager(Parcel in) {
        current = in.readParcelable(SiteEntry.class.getClassLoader());
        history = in.readParcelable(SiteTree.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(current, flags);
        dest.writeParcelable(history, flags);
    }

    public SiteEntry getCurrent(){ return current; }

    public SiteEntry[] getHistory(){ return history.asArray(); }

    public SiteTree getHistoryTree(){ return history; }

    public void setSiteHistory(SiteTree tree){
        if(history == null || history.size() < 1)
            history = tree;
        else
            history.addAll(tree);
    }

    public void addHistoryEntry(SiteEntry entry){
        history.add(entry);
    }

    public void updateSiteHistory(String url, String title){
        history.add(new SiteEntry(url, title, CustomTimeUtils.today()));
    }
    /**
     * Pushes the previous current entry onto the previous stack
     * @param newUrl - String url of the current page
     */
    public void setCurrent(String newUrl, String title, boolean eraseForwardStack){
        if(newUrl == null)
            return;

        current = new SiteEntry(newUrl, title, CustomTimeUtils.today());
        if(eraseForwardStack)
            clearForwardStack();

        addHistoryEntry(current);
    }

    public void clearBeforeDate(long date){
        if(date == -1){
            clear();
            return;
        }

        Log.i("HISTORY-CLEAR", String.format("History Size Before Clear: %d", history.size()));
        history.removeTail(date, true);
        Log.i("HISTORY-CLEAR", String.format("History Size After Clear: %d", history.size()));
    }

    public void clear(){
        history.clear();
    }

    public void clearForwardStack(){
        history.removeTail(current.rawDateCreated(), false);
    }

    public SiteEntry back(){
        SiteEntry back = history.lower(current);
        if(back == null)
            return current;

        current = back;
        addHistoryEntry(current);
        return current;
    }

    public SiteEntry next(){
        SiteEntry next = history.higher(current);
        if(next == null)
            return current;

        current = next;
        addHistoryEntry(current);
        return current;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HistoryManager> CREATOR = new Creator<HistoryManager>() {
        @Override
        public HistoryManager createFromParcel(Parcel in) {
            return new HistoryManager(in);
        }

        @Override
        public HistoryManager[] newArray(int size) {
            return new HistoryManager[size];
        }
    };
}
