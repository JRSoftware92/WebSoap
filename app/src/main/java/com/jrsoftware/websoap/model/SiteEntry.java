package com.jrsoftware.websoap.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jriley on 1/4/17.
 *
 * Parcelable model class for a site entry
 */

public class SiteEntry implements Comparable<SiteEntry>, Parcelable, Serializable {

    private long dateCreated;
    private String title, url;

    public SiteEntry(String url, String title, long dateCreated){
        this.url = url;
        this.title = title;
        this.dateCreated = dateCreated;
    }

    protected SiteEntry(Parcel in) {
        dateCreated = in.readLong();
        title = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dateCreated);
        dest.writeString(title);
        dest.writeString(url);
    }

    public String title(){ return title; }

    public String url(){ return url; }

    public long rawDateCreated(){ return dateCreated; }

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
        if(!(o instanceof SiteEntry))
            return false;
        return compareTo((SiteEntry)o) == 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Entry: { title='%s'; url ='%s'; };",
                title, url
        );
    }

    @Override
    public int compareTo(SiteEntry another) {
        if(another == null)
            return -1;

        if(another.dateCreated > this.dateCreated)
            return -1;
        else if(another.dateCreated < this.dateCreated)
            return 1;
        else
            return 0;
    }
}
