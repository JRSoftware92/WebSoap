package com.jrsoftware.websoap.controller;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteList;

import java.util.Arrays;
import java.util.Stack;

/**
 * Created by jriley on 1/4/17.
 *
 * Data Management Class for managing url history
 */

public class HistoryManager implements Parcelable {
    private static final String LOG_TAG = "HISTORY-MANAGER";
    private SiteEntry current;
    private Stack<SiteEntry> prevStack, nextStack;
    private SiteList olderHistory;

    public HistoryManager(){
        prevStack = new Stack<>();
        nextStack = new Stack<>();
        olderHistory = new SiteList();
        current = null;
    }

    public HistoryManager(SiteList history){
        prevStack = new Stack<>();
        nextStack = new Stack<>();
        olderHistory = history;
        current = null;
    }

    protected HistoryManager(Parcel in) {
        current = in.readParcelable(SiteEntry.class.getClassLoader());
        SiteEntry[] prevArr = in.createTypedArray(SiteEntry.CREATOR);
        SiteEntry[] nextArr = in.createTypedArray(SiteEntry.CREATOR);
        SiteEntry[] historyArr = in.createTypedArray(SiteEntry.CREATOR);

        prevStack = new Stack<>();
        nextStack = new Stack<>();
        olderHistory = new SiteList();

        Log.d(LOG_TAG, String.format("Start of List Size: %d", olderHistory.size()));

        prevStack.addAll(Arrays.asList(prevArr));
        nextStack.addAll(Arrays.asList(nextArr));
        olderHistory.addAll(historyArr);

        Log.d(LOG_TAG, "Array: ");
        for(SiteEntry entry : historyArr) {
            if(entry != null)
                Log.d(LOG_TAG, String.format("Title: %s; URL: %s", entry.title(), entry.url()));
            else
                Log.d(LOG_TAG, "NULL ENTRY FOUND");
        }

        Log.d(LOG_TAG, String.format("History Arr Load: %d", historyArr.length));
        Log.d(LOG_TAG, String.format("History List Load: %d", olderHistory.size()));
        Log.d(LOG_TAG, String.format("Are they equal? %s", olderHistory.size() == historyArr.length ? "Yes" : "No"));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        SiteEntry[] prevArr = new SiteEntry[prevStack.size()];
        SiteEntry[] nextArr = new SiteEntry[nextStack.size()];

        int i = 0;
        while(!prevStack.isEmpty())
            prevArr[i++] = prevStack.pop();

        i = 0;
        while(!nextStack.isEmpty())
            nextArr[i++] = nextStack.pop();

        int length = olderHistory.size();
        SiteEntry[] historyArr = new SiteEntry[length];
        for(i = 0; i < length; i++)
            historyArr[i] = olderHistory.get(i);

        dest.writeParcelable(current, flags);
        dest.writeTypedArray(prevArr, flags);
        dest.writeTypedArray(nextArr, flags);
        dest.writeTypedArray(historyArr, flags);

        Log.d(LOG_TAG, String.format("History Arr Write: %d", historyArr.length));
        Log.d(LOG_TAG, String.format("History List Write: %d", olderHistory.size()));
    }

    public SiteList getSiteHistory(){ return olderHistory; }

    public void setSiteHistory(SiteList list){
        if(olderHistory == null || olderHistory.size() < 1)
            olderHistory = list;
        else
            olderHistory.addAll(list);
    }

    public void addHistoryEntry(SiteEntry entry){
        olderHistory.add(entry);
    }

    public void updateSiteHistory(String url, String title){
        olderHistory.add(new SiteEntry(url, title));
    }

    public SiteEntry getLastRequestedURL(){
        return current;
    }

    /**
     * Pushes the previous current entry onto the previous stack
     * @param newUrl - String url of the current page
     */
    public void setCurrent(String newUrl, String title, boolean eraseForwardStack){
        if(newUrl == null)
            return;

        if(current != null && !newUrl.equals(current.url()))
            prevStack.add(current);

        current = new SiteEntry(newUrl, title);
        if(!nextStack.isEmpty() && eraseForwardStack)
            nextStack.clear();

        addHistoryEntry(current);
    }

    public SiteEntry back(){
        //Returns the current url if the stack is empty
        if(prevStack.isEmpty()) {
            return current;
        }

        //Push the current page onto the next stack
        if(current != null)
            nextStack.add(current);

        current = prevStack.pop();
        addHistoryEntry(current);
        return current;
    }

    public SiteEntry next(){
        //Returns the current url if the stack is empty
        if(nextStack.isEmpty()) {
            return current;
        }

        //Push the current page onto the previous stack
        if(current != null)
            prevStack.add(current);

        current = nextStack.pop();
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
