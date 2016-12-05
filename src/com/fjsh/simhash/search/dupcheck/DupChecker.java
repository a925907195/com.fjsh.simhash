package com.fjsh.simhash.search.dupcheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fjsh.simhash.search.dupcheck.core.HashDataAdapter;
import com.fjsh.simhash.search.dupcheck.core.SimHashAdapter;

public class DupChecker {

    public static List<String> cityList = new ArrayList<String>();
    
    public static void Init(List argCityList){
        cityList = argCityList;
    }
    
    public static long GetSimiHash(Map<String, Double> vector){
        return SimHashAdapter.GetSimiHash(vector);
    }
    
    public static boolean Contains(
            long srcSimHash,
            String info,
            DupCheckConfig config){
        return HashDataAdapter.Contains(srcSimHash, info, config);
    }
    
    public static void Add(long srcSimHash, String info, DupCheckConfig config){
        HashDataAdapter.Add(srcSimHash, info, config);
    }
    public static Map<Long, String> GetDups(
            long srcSimHash,
            DupCheckConfig config){
        return HashDataAdapter.GetDups(srcSimHash, config);
    }
}
