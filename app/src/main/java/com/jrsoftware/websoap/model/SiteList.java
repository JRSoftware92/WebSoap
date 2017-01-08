package com.jrsoftware.websoap.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jriley on 1/4/17.
 *
 * Parcelable Array List of SiteEntry objects
 */

public class SiteList extends ArrayList<SiteEntry> implements Parcelable, Serializable {

    private HashMap<String, Integer> urlMap;

    public SiteList(){
        super();
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
        arr = toArray(arr);

        dest.writeTypedArray(arr, flags);
    }

    public SiteEntry get(String url){
        int index = urlMap.get(url);
        return get(index);
    }

    @Override
    public SiteEntry set(int index, SiteEntry object) {
        SiteEntry original = get(index);
        urlMap.remove(original.url());

        urlMap.put(object.url(), index);
        return super.set(index, object);
    }

    @Override
    public boolean add(SiteEntry object) {
        if(object == null)
            return false;

        int index;
        //Removes previous entry
        if(urlMap.containsKey(object.url())){
            index = urlMap.get(object.url());
            super.remove(index);
        }

        //Adds New Entry at the bottom of the list
        super.add(object);
        urlMap.put(object.url(), size() - 1);

        return true;
    }

    @Override
    public void add(int index, SiteEntry object) {
        if(object == null || index < 0 || index >= size())
            return;

        //Adds entry at the specified index
        super.add(index, object);

        int size = size();
        SiteEntry temp;
        //Updates URL Map
        for(int i = index; i < size; i++){
            temp = get(i);
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

    public boolean addAll(List<SiteEntry> entries) {
        if(entries == null)
            return false;

        int length = entries.size();
        for(int i = 0; i < length; i++)
            add(entries.get(i));

        return true;
    }

    @Override
    public SiteEntry remove(int index) {
        SiteEntry obj = get(index);
        urlMap.remove(obj.url());
        return super.remove(index);
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
