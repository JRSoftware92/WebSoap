package com.jrsoftware.websoap.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by jriley on 1/8/17.
 *
 * TreeSet implementation for a list of Site entries
 */

public class SiteTree implements Parcelable, Serializable {
    private TreeSet<SiteEntry> entries;
    private HashMap<String, Long> urlMap;

    public SiteTree(){
        entries = new TreeSet<>();
        urlMap = new HashMap<>();
    }

    public SiteTree(SiteEntry[] entries){
        this.entries = new TreeSet<>();
        urlMap = new HashMap<>();
        addAll(entries);
    }

    public SiteTree(Collection<? extends SiteEntry> collection){
        this.entries = new TreeSet<>();
        urlMap = new HashMap<>();
        addAll(collection);
    }

    public SiteTree(SiteTree tree){
        this(tree.entries);
    }

    protected SiteTree(Parcel in) {
        entries = new TreeSet<>();
        urlMap = new HashMap<>();
        SiteEntry[] arr = in.createTypedArray(SiteEntry.CREATOR);
        addAll(Arrays.asList(arr));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        SiteEntry[] arr = asArray();
        dest.writeTypedArray(arr, flags);
    }

    public boolean add(SiteEntry entry){
        if(urlMap.containsKey(entry.url())) {
            Log.i("SITE-TREE-ADD", "Removing Old Entry");
            Long date = urlMap.get(entry.url());
            remove(date);
        }
        urlMap.put(entry.url(), entry.rawDateCreated());
        return entries.add(entry);
    }

    public boolean addAll(SiteEntry[] arr){
        if(arr == null)
            return false;
        return addAll(Arrays.asList(arr));
    }

    public boolean addAll(Collection<? extends SiteEntry> collection){
        Collection<? extends SiteEntry> copy = collection;
        boolean flag = false;
        for(SiteEntry entry : copy) {
            if(add(entry))
                flag = true;
        }
        return flag;
    }

    public boolean addAll(SiteTree obj){
        if(obj == null)
            return false;
        return addAll(obj.entries);
    }

    public boolean remove(long date){
        if(!urlMap.containsValue(date))
            return false;
        SiteEntry obj = entries.floor(new SiteEntry(null, null, date));
        if(obj == null)
            return false;

        urlMap.remove(obj.url());
        return entries.remove(obj);
    }

    public boolean remove(SiteEntry entry){
        if(urlMap.containsKey(entry.url()))
            urlMap.remove(entry.url());
        return entries.remove(entry);
    }

    public boolean removeTail(long earliestDate, boolean inclusive){
        SiteTree set = tailSet(earliestDate, inclusive);
        return entries.removeAll(set.entries);
    }

    public boolean contains(String url){ return urlMap.containsKey(url); }

    public boolean contains(SiteEntry entry){
        return contains(entry.url());
    }

    public boolean isEmpty(){
        return entries.isEmpty();
    }

    public int size(){
        return entries.size();
    }

    public void clear(){
        entries.clear();
        urlMap.clear();
    }

    public SiteEntry first(){
        return entries.first();
    }

    public SiteEntry last(){
        return entries.last();
    }

    public SiteEntry lower(long maxDate){
        return lower(new SiteEntry(null, null, maxDate));
    }

    public SiteEntry lower(SiteEntry exclusiveBound){
        return entries.lower(exclusiveBound);
    }

    public SiteEntry floor(long maxDate){
        return floor(new SiteEntry(null, null, maxDate));
    }

    public SiteEntry floor(SiteEntry inclusiveBound){
        return entries.floor(inclusiveBound);
    }

    public SiteEntry higher(long minDate){
        return higher(new SiteEntry(null, null, minDate));
    }

    public SiteEntry higher(SiteEntry exclusiveBound){
        return entries.higher(exclusiveBound);
    }

    public SiteEntry ceiling(long minDate){
        return ceiling(new SiteEntry(null, null, minDate));
    }

    public SiteEntry ceiling(SiteEntry inclusiveBound){
        return entries.ceiling(inclusiveBound);
    }

    public SiteTree subset(SiteEntry earliestEntry, boolean earliestInclusive, SiteEntry latestEntry,
                           boolean latestInclusive){
        SortedSet<SiteEntry> sortedSet = entries.subSet(earliestEntry, earliestInclusive, latestEntry, latestInclusive);
        return new SiteTree(sortedSet);
    }

    public SiteTree headSet(long latestDate, boolean inclusive){
        return headSet(new SiteEntry(null, null, latestDate), inclusive);
    }

    public SiteTree headSet(SiteEntry latestEntry, boolean inclusive){
        SortedSet<SiteEntry> sortedSet = entries.headSet(latestEntry, inclusive);
        return new SiteTree(sortedSet);
    }

    public SiteTree tailSet(long earliestDate, boolean inclusive){
        return tailSet(new SiteEntry(null, null, earliestDate), inclusive);
    }

    public SiteTree tailSet(SiteEntry earliestEntry, boolean inclusive){
        SortedSet<SiteEntry> sortedSet = entries.tailSet(earliestEntry, inclusive);
        return new SiteTree(sortedSet);
    }

    public SiteEntry[] asArray(){
        SiteEntry[] output = new SiteEntry[size()];
        entries.toArray(output);

        return output;
    }

    public ArrayList<SiteEntry> asArrayList(){
        ArrayList<SiteEntry> list = new ArrayList<>(size());
        list.addAll(entries);

        return list;
    }

    public SiteEntry[] asArrayReversed(){
        NavigableSet<SiteEntry> set = entries.descendingSet();
        SiteEntry[] output = new SiteEntry[set.size()];
        set.toArray(output);

        return output;
    }

    public ArrayList<SiteEntry> asArrayListReversed(){
        ArrayList<SiteEntry> list = asArrayList();
        Collections.reverse(list);

        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SiteTree> CREATOR = new Creator<SiteTree>() {
        @Override
        public SiteTree createFromParcel(Parcel in) {
            return new SiteTree(in);
        }

        @Override
        public SiteTree[] newArray(int size) {
            return new SiteTree[size];
        }
    };
}
