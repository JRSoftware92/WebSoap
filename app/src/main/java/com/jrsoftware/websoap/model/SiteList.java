package com.jrsoftware.websoap.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jriley on 1/4/17.
 *
 * Parcelable Array List of SiteEntry objects
 *
 * TODO - Create HashMap of Indeces for quick url referencing
 */

public class SiteList extends ArrayList<SiteEntry> implements Parcelable, Serializable {

    private HashMap<String, Integer> urlMap;

    public SiteList(){
        super();
        urlMap = new HashMap<>();
    }

    public SiteList(int initialSize){
        super(initialSize);
        urlMap = new HashMap<>(initialSize);
    }

    public SiteList(SiteEntry[] bookmarks){
        super();
        initialize(bookmarks);
    }

    protected SiteList(Parcel in) {
        SiteEntry[] arr = in.createTypedArray(SiteEntry.CREATOR);
        if(arr != null)
            initialize(arr);
    }

    private void initialize(SiteEntry[] bookmarks){
        urlMap = new HashMap<>();
        if(bookmarks == null)
            return;

        int length = bookmarks.length;
        if(length > 0){
            for(int i = 0; i < length; i++)
                add(bookmarks[i]);
        }
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

        super.remove(index);
        urlMap.put(object.url(), index);
        super.add(index, object);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        SiteEntry[] arr = new SiteEntry[size()];
        arr = toArray(arr);

        dest.writeTypedArray(arr, flags);
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
