package com.fjsh.simhash.search.dupcheck.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fjsh.simhash.search.common.Common;
import com.fjsh.simhash.search.dupcheck.DupCheckConfig;
import com.fjsh.simhash.search.dupcheck.DupChecker;
import com.fjsh.simhash.search.dupcheck.jedb.JEDBManager;
import com.fjsh.simhash.search.dupcheck.jedb.MemoryHashManager;
import com.fjsh.simhash.search.dupcheck.jedb.PageData;
import com.fjsh.simhash.search.dupcheck.jedb.PageEntity;

public class HashDataAdapter {


    public static void Add(long srcSimHash, String srcURL, DupCheckConfig config){
        // 添加hash到列表.
        for(TableData table : config.tableList){
            
            //
            // 解析信息
            //
            
            // 掩码为1的位，作为头。
            long leadHashLong  = GetHash(srcSimHash, table.maskList, true);
            // 掩码为0的位，作为尾。
            long restHashLong  = GetHash(srcSimHash, table.maskList, false);
            // 转换为Int. 注意restHashLong有效bit位低于32。
            //int restHashInt = (int)restHashLong;

            // 获得ID. 置为头三位。
            for(int t=0; t < 3; t++){
                leadHashLong = Common.SetBit(table.ID, leadHashLong, t + 1, 30 + t);
            }
            int key = (int)leadHashLong;
            
            //
            // 存储hash.
            //
            if(config.useTableMemoryCache == true){
                table.leadMap.put(key, true);
                // TOOD: 量少时可以放在内存中。。。
                if(config.useHashMemoryCache == true){
                    MemoryHashManager.Add(key, restHashLong, srcURL);                                         
                }
                else{
                    JEDBManager.Add(key, restHashLong, srcURL);
                }
            }
            else{
                // 直接存储到BDB/hadoop.
                JEDBManager.Add(key, restHashLong, srcURL);
            }
        }
    }

    public static boolean Contains(
            long srcSimHash,
            String argURL,
            DupCheckConfig config){
        
        boolean contains = false;
        
        for(TableData table : config.tableList){
            long leadHashLong  = GetHash(srcSimHash, table.maskList, true);
            
            // 掩码为0的位，作为尾。
            long restHashLong  = GetHash(srcSimHash, table.maskList, false);
            // 转换为Int. 注意restHashLong有效bit位低于32。
            //int restHashInt = (int)restHashLong;
            
            // 获得ID. 置为头三位。
            for(int t=0; t < 3; t++){
                leadHashLong = Common.SetBit(table.ID, leadHashLong, t + 1, 30 + t);
            }
            int key = (int)leadHashLong;
            
            // Get matching hashlist.
            List hashList = null;
            if(config.useTableMemoryCache){
                if(table.leadMap.containsKey(key) == true){

                    if(config.useHashMemoryCache){
                        hashList = MemoryHashManager.Get(key);
                    }
                    else{
                        PageEntity en = JEDBManager.Get(key); 
                        if(en != null){
                            hashList = en.dataList;
                        }
                    }
                }
            }
            else{
                PageEntity en =JEDBManager.Get(key); 
                if(en != null){
                    hashList = en.dataList;
                }
            }
            
            if(hashList != null){
                // 检查是否有相似。
                boolean simi = false;
                if(config.keepDetail){
                    simi = ExistSimiliarDetail(restHashLong, argURL, hashList, config.K);
                    
                }
                else{
                    simi = ExistSimiliar(restHashLong, hashList, config.K);    
                }
                if(simi == true){
                    contains = true;
                    
                    break;
                }
            }
        }
        
        return contains;
    }
  
    public static Map<Long, String> GetDups(
            long srcSimHash,
            DupCheckConfig config){

    	Map<Long, String> dupInfos = new HashMap<Long, String>();
    	
        if(!config.keepDetail) return null;
        
        for(TableData table : config.tableList){
            long leadHashLong  = GetHash(srcSimHash, table.maskList, true);
            
            // 掩码为0的位，作为尾。
            long restHashLong  = GetHash(srcSimHash, table.maskList, false);
            // 转换为Int. 注意restHashLong有效bit位低于32。
            //int restHashInt = (int)restHashLong;
            
            // 获得ID. 置为头三位。
            for(int t=0; t < 3; t++){
                leadHashLong = Common.SetBit(table.ID, leadHashLong, t + 1, 30 + t);
            }
            int key = (int)leadHashLong;
            
            // Get matching hashlist.
            List hashList = null;
            if(config.useTableMemoryCache){
                if(table.leadMap.containsKey(key) == true){

                    if(config.useHashMemoryCache){
                        hashList = MemoryHashManager.Get(key);
                    }
                    else{
                        PageEntity en = JEDBManager.Get(key); 
                        if(en != null){
                            hashList = en.dataList;
                        }
                    }
                }
            }
            else{
                PageEntity en =JEDBManager.Get(key); 
                if(en != null){
                    hashList = en.dataList;
                }
            }
            
            if(hashList != null){
                // 检查是否有相似。
                for(Object o :hashList){
                	PageData pageData = (PageData)o;
                    // 判断是否有K位不同. 
                    int dist = GetHammingDistance( restHashLong, pageData.hash);
                    if(dist <= config.K){
                    	long origHash=RecoverHash(leadHashLong, pageData.hash, table.maskList);
                    	//System.out.println("recover:"+Long.toBinaryString(leadHashLong));
                    	//System.out.println("pageData.hash:"+Long.toBinaryString(pageData.hash));
                    	//System.out.println("recover:"+Long.toBinaryString(origHash));
                    	if(!dupInfos.containsKey(origHash)) dupInfos.put(origHash, pageData.info);
                    }
                }
                
            }
        }
        
        return dupInfos;
    }
    
    private static boolean ExistSimiliarDetail(long srcRestHash, String argURL, List<PageData> hashList, int K){
        boolean simi = false;

        for(PageData pageData : hashList){
            // 判断是否有K位不同. 
            int dist = GetHammingDistance( srcRestHash, pageData.hash);
            if(dist <= K){
                simi = true;
                break;
            }
        }

        return simi;                  
    }
    
    private static boolean ExistSimiliar(long srcRestHash, List<Long> hashList, int K){
        boolean simi = false;

        for(Long restHash : hashList){
            // 判断是否有K位不同. 
            int dist = GetHammingDistance( srcRestHash, restHash);
            if(dist <= K){
                simi = true;
                break;
            }
        }

        return simi;
    }
    
    
    private static String ParseCity(String argURL){
        String dstCityShort = "";
        argURL = argURL.toLowerCase();
        for(String cityShort: DupChecker.cityList){
            if(    argURL.startsWith(cityShort + ".") ||
                    argURL.indexOf("://"+cityShort + ".")> -1 ||
                    argURL.indexOf("."+cityShort + ".")> -1 ){
                dstCityShort =cityShort;
                break;
            }
        }
        
        return dstCityShort;
    }
    
    public static int GetHammingDistance(int src, int dst){
        int dist = 0;
        int xor = src ^ dst;
        // 统计有多少个1.
        int mask = 1;
        for(int t=0; t < 32 ; t++){
            if( (xor & mask) > 0){
                dist++;
            }
            mask <<=1;
        }

        return dist;
    }
    
    public static int GetHammingDistance(long src, long dst){
        int dist = 0;
        long xor = src ^ dst;
        // 统计有多少个1.
        long mask = 1;
        for(int t=0; t <64 ; t++){
            if( (xor & mask) > 0){
                dist++;
            }
            mask <<=1;
        }

        return dist;
    }
    
    private static long GetHash(long srcSimHash, boolean[] maskList, boolean head){
        long restHashLong = 0;
        int bitIndex = 1;
        for(int i=0; i <maskList.length; i++){
            if( (maskList[i] == true && head == true) ||
                (maskList[i] == false && head == false)){
                restHashLong = Common.SetBit(srcSimHash, restHashLong, i+1,bitIndex );
                bitIndex++;
            }
        }
        
        return restHashLong;
    }
    
    public static long RecoverHash(long leadSimHash,long restSimHash, boolean[] maskList){
        long origHashLong = 0;
        int bitIndexLead = 1;
        int bitIndexRest = 1;
        for(int i=0; i <maskList.length; i++)
        {
        	if(i==12)
        	{
        		int deb=1;
        		deb++;
        	}
            if(maskList[i] == true)
            {
		    	origHashLong = Common.SetBit(leadSimHash, origHashLong, bitIndexLead, i+1);
		    	bitIndexLead++;
            }
		    else
		    {
		    	origHashLong = Common.SetBit(restSimHash, origHashLong, bitIndexRest, i+1);
		    	bitIndexRest++;
		    }
    	}    
    
        return origHashLong;
    }
    
    private static void Test(){
        long simi = Long.parseLong("7658409153219135218");
        
        boolean[] maskList = new boolean[64];
        for(int t=0; t < maskList.length; t++){
            if(t >=13 &&t<=25){
                maskList[t] = true;
            }
            else{
                maskList[t] = false;
            }
        }

        Common.Println("simi:" + Long.toBinaryString(simi));
        
        long leadHashLong  = GetHash(simi, maskList, true);
        long restHashLong  = GetHash(simi, maskList, false);
        Common.Println("leadHashLong:" + Long.toBinaryString(leadHashLong));
        Common.Println("restHashLong:" + Long.toBinaryString(restHashLong));
        long rec = RecoverHash(leadHashLong, restHashLong, maskList);
        Common.Println("recover_simihash:" + Long.toBinaryString(rec));
        Common.Println("simi=recover_simihash?:"+(simi==rec));
    }
    
    public static void main(String[] args) {
        Test();
    }

}
