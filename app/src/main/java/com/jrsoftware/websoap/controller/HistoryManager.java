package com.jrsoftware.websoap.controller;

import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteList;

import java.util.Stack;

/**
 * Created by jriley on 1/4/17.
 *
 * Data Management Class for managing url history
 */

public class HistoryManager {
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

    public SiteEntry getLastRequestedURL(){
        return current;
    }

    public SiteList getSiteHistory(){ return olderHistory; }

    public void setSiteHistory(SiteList list){ olderHistory = list; }

    /**
     * Pushes the previous current entry onto the previous stack
     * @param newUrl - String url of the current page
     */
    public void setCurrent(String newUrl, String title, boolean eraseForwardStack){
        if(current != null && !current.equals(newUrl))
            prevStack.add(current);

        current = new SiteEntry(newUrl, title);
        if(!nextStack.isEmpty() && eraseForwardStack)
            nextStack.clear();

        addHistoryEntry(current);
    }

    public SiteEntry back(){
        //Returns the current url if the stack is empty
        if(prevStack.isEmpty())
            return current;

        //Push the current page onto the next stack
        if(current != null)
            nextStack.add(current);

        current = prevStack.pop();
        addHistoryEntry(current);
        return current;
    }

    public SiteEntry next(){
        //Returns the current url if the stack is empty
        if(nextStack.isEmpty())
            return current;

        //Push the current page onto the previous stack
        if(current != null)
            prevStack.add(current);

        current = nextStack.pop();
        addHistoryEntry(current);
        return current;
    }

    public void addHistoryEntry(SiteEntry entry){
        olderHistory.add(entry);
    }

    public SiteEntry[] getHistoryArray(){
        Stack<SiteEntry> tempStack = prevStack;
        tempStack.push(current);
        SiteEntry[] prevArr = new SiteEntry[tempStack.size()];
        SiteEntry[] nextArr = new SiteEntry[nextStack.size()];

        prevArr = tempStack.toArray(prevArr);
        nextArr = nextStack.toArray(nextArr);

        if(prevArr.length < 1)
            return nextArr;
        else if(nextArr.length < 1)
            return prevArr;
        else{
            SiteEntry[] output = new SiteEntry[prevArr.length + nextArr.length];
            int lengthPrev = prevArr.length;
            int lengthNext = nextArr.length;
            for(int i = 0; i < lengthPrev; i++)
                output[i] = prevArr[i];

            for(int i = 0; i < lengthNext; i++)
                output[i + lengthPrev] = nextArr[i];

            return output;
        }
    }

    public SiteList getHistoryList(){
        SiteEntry[] entries = getHistoryArray();
        if(entries == null)
            return null;

        int length = entries.length;
        SiteList list = new SiteList(length);
        for(int i = 0; i < length; i++)
            list.add(entries[i]);

        return list;
    }
}
