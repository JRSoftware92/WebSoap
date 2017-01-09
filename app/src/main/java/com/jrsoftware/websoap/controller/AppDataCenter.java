package com.jrsoftware.websoap.controller;

import android.content.Context;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteList;
import com.jrsoftware.websoap.model.SiteTree;
import com.jrsoftware.websoap.util.CustomTimeUtils;

import java.io.IOException;

/**
 * Created by jriley on 1/4/17.
 *
 * Primary Controller for accessing Application Data
 */

public class AppDataCenter {
    private Context context;

    private SiteTree bookmarks;
    private HistoryManager historyManager;
    private FileManager fileManager;

    public AppDataCenter(Context context){
        this.context = context;
        bookmarks = new SiteTree();
        historyManager = new HistoryManager();
        fileManager = new FileManager(context);
    }

    public Context getContext(){ return context; }

    public void setContext(Context context){ this.context = context; }

    public String getLastRequestedURL(){
        SiteEntry site = historyManager.getCurrent();
        if(site == null)
            return null;
        else
            return site.url();
    }

    public SiteEntry getLastRequestedSite(){
        return historyManager.getCurrent();
    }

    public SiteTree getBookmarks(){
        return bookmarks;
    }

    public SiteTree getSiteHistory(){ return historyManager.getHistoryTree(); }

    public HistoryManager getHistoryManager() { return historyManager; }

    public void setCurrentSite(String url, String title, boolean eraseForwardStack){
        historyManager.setCurrent(url, title, eraseForwardStack);
    }

    public void setSiteHistory(SiteTree siteHistory) {
        historyManager.setSiteHistory(siteHistory);
    }

    public void setSiteHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public void updateSiteTitle(String url, String title){
        historyManager.updateSiteHistory(url, title);
    }

    public String backURL(){
        SiteEntry site = historyManager.back();
        if(site == null)
            return null;
        else
            return site.url();
    }

    public SiteEntry previousSite(){
        return historyManager.back();
    }

    public String nextURL(){
        SiteEntry site = historyManager.next();
        if(site == null)
            return null;
        else
            return site.url();
    }

    public SiteEntry nextSite(){
        return historyManager.next();
    }

    public void clearHistory(String timePeriodStr) throws IOException {
        long value;
        switch(timePeriodStr){
            case "1h":
                value = CustomTimeUtils.hoursFromNow(-1);
                break;
            case "2h":
                value = CustomTimeUtils.hoursFromNow(-2);
                break;
            case "6h":
                value = CustomTimeUtils.hoursFromNow(-6);
                break;
            case "12h":
                value = CustomTimeUtils.hoursFromNow(-12);
                break;
            case "1d":
                value = CustomTimeUtils.daysFromToday(-1);
                break;
            case "2d":
                value = CustomTimeUtils.daysFromToday(-2);
                break;
            case "1w":
                value = CustomTimeUtils.weeksFromToday(-1);
                break;
            case "1m":
                value = CustomTimeUtils.monthsFromToday(-1);
                break;
            case "2m":
                value = CustomTimeUtils.monthsFromToday(-2);
                break;
            case "6m":
                value = CustomTimeUtils.monthsFromToday(-6);
                break;
            case "1y":
                value = CustomTimeUtils.yearsFromToday(-1);
                break;
            case "all-time":
            default:
                value = -1;
                break;
        }


        //FIXME - Not Saving Cleared History
        historyManager.clearBeforeDate(value);
        saveHistory();
    }

    public void addBookmark(String url, String title){
        bookmarks.add(new SiteEntry(url, title, CustomTimeUtils.today()));
    }

    public void setBookmarks(SiteTree bookmarks) {
        this.bookmarks = bookmarks;
    }

    //Only Checks Internal Files currently
    public boolean fileExists(String filePath, String dir){
        if(dir == null)
            dir = fileManager.getInternalDirectory();
        else
            dir = fileManager.toInternalPath(dir);

        return fileManager.exists(dir + filePath);
    }

    public void saveHistory() throws IOException {
        SiteTree history = historyManager.getHistoryTree();
        String filePath = context.getString(R.string.file_history);

        //TODO - Encrypt file
        fileManager.writeInternalSerializable(history, filePath, null);
    }

    public void loadHistory() throws IOException, ClassNotFoundException {
        String filePath = context.getString(R.string.file_history);
        SiteTree history = (SiteTree) fileManager.readInternalSerializable(filePath, null);

        if(historyManager == null)
            historyManager = new HistoryManager(history);
        else
            historyManager.setSiteHistory(history);
    }

    public void saveBookmarks() throws IOException {
        String filePath = context.getString(R.string.file_bookmarks);

        //TODO - Encrypt File
        fileManager.writeInternalSerializable(bookmarks, filePath, null);
    }

    public void loadBookmarks() throws IOException, ClassNotFoundException {
        String filePath = context.getString(R.string.file_bookmarks);
        bookmarks = (SiteTree) fileManager.readInternalSerializable(filePath, null);
    }
}

