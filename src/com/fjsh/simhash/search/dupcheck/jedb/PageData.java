package com.fjsh.simhash.search.dupcheck.jedb;

import com.sleepycat.persist.model.Persistent;


@Persistent
public class PageData implements Comparable<PageData> {
    public Long hash;
    public String info;
    
//    public PageData(Integer argHash, String argUrl){
//        hash = argHash;
//        url = argUrl;
//    }
    
    @Override
    public int compareTo(PageData o) {
        return hash.compareTo(o.hash);
    }
    
    public static PageData NewInst(Long argHash, String argInfo){
        PageData data = new PageData();
        data.info = argInfo;
        data.hash = argHash;
        
        return data;
        
    }
}
