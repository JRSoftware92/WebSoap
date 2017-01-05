package com.jrsoftware.websoap.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by jriley on 1/4/17.
 *
 * Serializable Array List of Bookmarks
 */

public class SiteList extends ArrayList<SiteEntry> implements Parcelable {

    public SiteList(){
        super();
    }

    public SiteList(int initialSize){
        super(initialSize);
    }

    public SiteList(SiteEntry[] bookmarks){
        super();
        initialize(bookmarks);
    }

    protected SiteList(Parcel in) {
        SiteEntry[] arr = (SiteEntry[]) in.readParcelableArray(SiteEntry.class.getClassLoader());
        initialize(arr);
    }

    private void initialize(SiteEntry[] bookmarks){
        if(bookmarks == null)
            return;

        if(bookmarks.length > 0){
            for(SiteEntry bookmark : bookmarks)
                add(bookmark);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(size() < 1)
            return;

        SiteEntry[] arr = new SiteEntry[size()];
        arr = toArray(arr);

        dest.writeParcelableArray(arr, flags);
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
