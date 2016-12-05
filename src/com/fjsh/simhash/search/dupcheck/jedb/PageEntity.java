package com.fjsh.simhash.search.dupcheck.jedb;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;



@Entity
public class PageEntity {
    @PrimaryKey
    public String key;               // TableID + PID
    public List dataList; // list of rest hash bits.
    
    PageEntity(int argKey,
            List argDatalList) {
        key = String.valueOf(argKey);
        dataList = argDatalList;
    }
    
    
    private PageEntity() {} // For deserialization
    
}

