package com.fjsh.simhash.search.dupcheck.jedb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MemoryHashManager {
    
	public static Map<Integer, List> hashMap = 
         Collections.synchronizedMap(new HashMap<Integer, List>());
    // TODO: 存在为空的List？？？
    // TODO: 暂未支持srcURL
    public static void Add(int argKey, Long argHash, String argURL){
        List  restHashList = hashMap.get(argKey);
        if(restHashList == null){
            if(argURL == null){
                restHashList  = Collections.synchronizedList(new ArrayList<Long>());
            }
            else{
                restHashList  = Collections.synchronizedList(new ArrayList<PageData>());
            }
                
            hashMap.put(argKey, restHashList);

        }

        if(argURL == null){
            restHashList.add(argHash);
        }
        else{
            restHashList.add(PageData.NewInst(argHash, argURL));
        }
    }
    
    public static List  Get(int argKey){
        return hashMap.get(argKey);    
    }
    
    // Save
    
    // Load
    
    // Delete
    
}
