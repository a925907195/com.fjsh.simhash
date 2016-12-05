package com.fjsh.simhash.search.dupcheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fjsh.simhash.search.dupcheck.core.TableData;

public class DupCheckConfig {

    public boolean useTableMemoryCache = true; // 是否把表缓存在内存。太大时不缓存，使用hadoop等。
    public boolean useHashMemoryCache = true;  // 是否把Hash缓存在内存。太大时不缓存，使用hadoop等。
    public int K = 3;
    public List<TableData> tableList   = new ArrayList<TableData>();
    
    public boolean keepDetail = false;
    
    public void InitTable_5(){
        //
        int[] blockBits = new int[]{13, 13, 13, 13, 12};

        int tableID = 1;
        for(int m=0; m < blockBits.length - 1; m++){
            for(int n=m + 1; n < blockBits.length ; n++){
                TableData table = new TableData();

                table.ID = tableID++;
                
                table.leadLength = blockBits[m] + blockBits[m];
                
                // 生成mask
                int offset = 0;
                for(int t=0; t < m; t++){
                    offset += blockBits[t];    
                }
                
                for(int t=0; t < blockBits[m]; t++){
                    table.maskList[offset + t] = true;                        
                }

                offset = 0;
                for(int t=0; t < n; t++){
                    offset += blockBits[t];    
                }
                
                for(int t=0; t < blockBits[n]; t++){
                    table.maskList[offset + t] = true;                        
                }
                 
                // 索引序列
//                StringBuilder sb = new StringBuilder();
//                for(int k=0; k < table.maskList.length; k++){
//                    if(table.maskList[k]){
//                        sb.append("####" + "_");                        
//                    }
//                    else{
//                        sb.append("++++" + "_");
//                    }
//                }
//                System.out.println(sb.toString());
                tableList.add(table);
            }
        }
    }
    
    public static void main(String[] args){
        DupCheckConfig config = new DupCheckConfig();
        config.InitTable_5();
    }
}
