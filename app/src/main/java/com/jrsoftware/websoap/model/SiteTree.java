package com.jrsoftware.websoap.model;

import java.util.TreeSet;

/**
 * Created by jriley on 1/8/17.
 */

public class SiteTree {
    private TreeSet<SiteEntry> entries;

    public SiteTree(){
        entries = new TreeSet<>();
    }
}
