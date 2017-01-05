package com.jrsoftware.websoap.controller;

import android.content.Context;

import com.jrsoftware.websoap.model.FileManager;
import com.jrsoftware.websoap.model.HistoryManager;

/**
 * Created by jriley on 1/4/17.
 *
 * Primary Controller for accessing Application Data
 */

public class AppDataCenter {
    private Context context;

    private HistoryManager historyManager;
    private FileManager fileManager;

    public AppDataCenter(Context context){
        this.context = context;
        historyManager = new HistoryManager();
        fileManager = new FileManager(context);
    }

    public Context getContext(){ return context; }

    public void setContext(Context context){ this.context = context; }

    public String getLastRequestedURL(){
        return historyManager.getLastRequestedURL();
    }

    public void setCurrentURL(String url, boolean eraseForwardStack){
        historyManager.setCurrent(url, eraseForwardStack);
    }

    public String backURL(){
        return historyManager.back();
    }

    public String nextURL(){
        return historyManager.next();
    }
}

