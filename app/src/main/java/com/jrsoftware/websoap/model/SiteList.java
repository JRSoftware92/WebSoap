package com.jrsoftware.websoap.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by jriley on 1/4/17.
 *
 * Parcelable Array List of SiteEntry objects
 */

@Deprecated
public class SiteList implements Parcelable, Serializable {

    private ArrayList<SiteEntry> entries;
    private HashMap<String, Integer> urlMap;

    public SiteList(){
        entries = new ArrayList<>();
        urlMap = new HashMap<>();
    }

    protected SiteList(Parcel in) {
        SiteEntry[] arr = in.createTypedArray(SiteEntry.CREATOR);
        if(arr != null)
            initialize(arr);
    }

    private void initialize(SiteEntry[] entries){
        urlMap = new HashMap<>();
        if(entries == null)
            return;

        addAll(entries);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        SiteEntry[] arr = new SiteEntry[size()];
        arr = entries.toArray(arr);

        dest.writeTypedArray(arr, flags);
    }

    public int size(){
        return entries.size();
    }

    public SiteEntry get(int index){
        return entries.get(index);
    }

    public SiteEntry get(String url){
        int index = urlMap.get(url);
        return entries.get(index);
    }

    public SiteEntry set(int index, SiteEntry object) {
        SiteEntry original = entries.get(index);
        urlMap.remove(original.url());

        urlMap.put(object.url(), index);
        return entries.set(index, object);
    }

    public boolean add(SiteEntry object) {
        if(object == null)
            return false;

        int index;
        //Removes previous entry
        if(urlMap.containsKey(object.url())){
            index = urlMap.get(object.url());
            entries.remove(index);
        }

        //Adds New Entry at the bottom of the list
        entries.add(object);
        urlMap.put(object.url(), size() - 1);

        return true;
    }

    public void add(int index, SiteEntry object) {
        if(object == null || index < 0 || index >= size())
            return;

        //Adds entry at the specified index
        entries.add(index, object);

        int size = size();
        SiteEntry temp;
        //Updates URL Map
        for(int i = index; i < size; i++){
            temp = entries.get(i);
            urlMap.put(temp.url(), i);
        }
    }

    public boolean addAll(SiteEntry[] entries) {
        if(entries == null)
            return false;

        int length = entries.length;
        for(int i = 0; i < length; i++)
            add(entries[i]);

        return true;
    }

    public boolean addAll(SiteList entries) {
        if(entries == null)
            return false;

        int length = entries.size();
        for(int i = 0; i < length; i++)
            add(entries.get(i));

        return true;
    }

    public SiteEntry remove(int index) {
        SiteEntry obj = entries.get(index);
        urlMap.remove(obj.url());
        return entries.remove(index);
    }

    public void reverse(){
        Collections.reverse(entries);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SiteList> CREATOR = new Creator<SiteList>() {
        @Override
        public SiteList createFromParcel(Parcel in) {
            return new SiteList(in);
        }

        @Override
        public SiteList[] newArray(int size) {
            return new SiteList[size];
        }
    };
}
