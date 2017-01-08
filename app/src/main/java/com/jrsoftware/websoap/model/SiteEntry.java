package com.jrsoftware.websoap.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jriley on 1/4/17.
 *
 * Parcelable model class for a site entry
 */

public class SiteEntry implements Parcelable, Serializable {

    private String title, url;

    public SiteEntry(String url, String title){
        this.url = url;
        this.title = title;
    }

    protected SiteEntry(Parcel in) {
        title = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
    }

    public String title(){ return title; }

    public String url(){ return url; }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SiteEntry> CREATOR = new Creator<SiteEntry>() {
        @Override
        public SiteEntry createFromParcel(Parcel in) {
            return new SiteEntry(in);
        }

        @Override
        public SiteEntry[] newArray(int size) {
            return new SiteEntry[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(!(o instanceof SiteEntry))
            return false;

        SiteEntry obj = (SiteEntry) o;
        return obj.url.equals(this.url);
    }
}
