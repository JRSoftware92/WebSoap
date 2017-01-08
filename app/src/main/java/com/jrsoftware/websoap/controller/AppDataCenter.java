package com.jrsoftware.websoap.controller;

import android.content.Context;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteList;

import java.io.IOException;

/**
 * Created by jriley on 1/4/17.
 *
 * Primary Controller for accessing Application Data
 */

public class AppDataCenter {
    private Context context;

    private SiteList bookmarks;
    private HistoryManager historyManager;
    private FileManager fileManager;

    public AppDataCenter(Context context){
        this.context = context;
        bookmarks = new SiteList();
        historyManager = new HistoryManager();
        fileManager = new FileManager(context);
    }

    public Context getContext(){ return context; }

    public void setContext(Context context){ this.context = context; }

    public String getLastRequestedURL(){
        SiteEntry site = historyManager.getLastRequestedURL();
        if(site == null)
            return null;
        else
            return site.url();
    }

    public SiteEntry getLastRequestedSite(){
        return historyManager.getLastRequestedURL();
    }

    public SiteList getBookmarks(){
        return bookmarks;
    }

    public SiteList getSiteHistory(){ return historyManager.getSiteHistory(); }

    public HistoryManager getHistoryManager() { return historyManager; }

    public void setCurrentSite(String url, String title, boolean eraseForwardStack){
        historyManager.setCurrent(url, title, eraseForwardStack);
    }

    public void setSiteHistory(SiteList siteHistory) {
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

    public SiteEntry getBookmark(int index){
        if(index < 0 || bookmarks.size() <= index)
            return null;
        return bookmarks.get(index);
    }

    public void addBookmark(String url, String title){
        bookmarks.add(new SiteEntry(url, title));
    }

    public void addBookmark(int index, String url, String title){
        bookmarks.add(index, new SiteEntry(url, title));
    }

    public void removeBookmark(int index){
        if(index < 0 || bookmarks.size() <= index)
            return;
        bookmarks.remove(index);
    }

    public void setBookmarks(SiteList bookmarks) {
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
        SiteList history = historyManager.getSiteHistory();
        String filePath = context.getString(R.string.file_history);

        //TODO - Encrypt file
        if(history.size() > 1)
            fileManager.writeInternalSerializable(history, filePath, null);
    }

    public void loadHistory() throws IOException, ClassNotFoundException {
        String filePath = context.getString(R.string.file_history);
        SiteList history = (SiteList) fileManager.readInternalSerializable(filePath, null);

        if(historyManager == null)
            historyManager = new HistoryManager(history);
        else
            historyManager.setSiteHistory(history);
    }

    public void saveBookmarks() throws IOException {
        String filePath = context.getString(R.string.file_bookmarks);

        //TODO - Encrypt File
        if(bookmarks.size() > 0)
            fileManager.writeInternalSerializable(bookmarks, filePath, null);
    }

    public void loadBookmarks() throws IOException, ClassNotFoundException {
        String filePath = context.getString(R.string.file_bookmarks);
        bookmarks = (SiteList) fileManager.readInternalSerializable(filePath, null);
    }
}

