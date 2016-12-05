package com.fjsh.simhash.search.dupcheck.jedb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;









import com.fjsh.simhash.search.common.Common;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class JEDBManager {
    
    public static Object dataLock = new Object();
    private static int cache_size = 30*1024*1024;
    private static String jeStoreName = "jeStore";
    public static String folderPath = "./DUP_JEDB";
//    private static String jeStoreFolder = "JEDB";
//    private static String[] rootPathList = new String[]{
//                                                            "/temp",// TODO: tmp.
//                                                            "\"%USERPROFILE%/Local Settings/Temp\"",
//                                                            "."
//                                                        };
    
    private static File envHome = null;

    private static Environment envmnt= null;
    private static EntityStore store= null;
    private static PrimaryIndex<String, PageEntity> pIdx = null;

    public static void Restart(){
        Restart(folderPath,cache_size);
    }
    
    public static void Restart(String argfolderPath, int cacheSize){
    	cache_size = cacheSize;
        synchronized (JEDBManager.dataLock){
            if(store != null){
                store.close();    
            }
            
            if (envmnt != null){
                envmnt.close();    
            }
            
            pIdx = null;
            
            Do_Start(argfolderPath);
        }
    }
    
    public static void Do_Start(String argfolderPath){
        folderPath = argfolderPath;
        CreateDBFolder(folderPath);
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setCacheSize(cache_size);
        StoreConfig storeConfig = new StoreConfig();
        envConfig.setAllowCreate(true);
        storeConfig.setAllowCreate(true);
        if(envHome.canWrite() == false){
            return;
        }
        
        // Open the environment and entity store
        envmnt = new Environment(envHome, envConfig);
        store = new EntityStore(envmnt, jeStoreName, storeConfig);

        // Primary key for SimpleEntityClass classes
        pIdx = store.getPrimaryIndex(String.class, PageEntity.class);
        System.out.println("init pIdx");
        if(pIdx == null){
            System.out.println("init : pIdx == null");
        }
        
        System.out.println("CacheSize:" +envConfig.getCacheSize());
        System.out.println("CachePercent:" + envConfig.getCachePercent());
    }

    private static void CreateDBFolder(String folderPath){
         try{
            envHome = new File(folderPath);//�����ļ�·��          
            if(envHome.exists() && envHome.isDirectory()){//�ж����ļ�����Ŀ¼   
               envHome.delete();
            }
            envHome.mkdir();
            System.out.println(" JEDB path." + folderPath);
        }
        catch(SecurityException e){
            System.out.println("ERROR: JEDB path not accessible." + folderPath );
        }
    }
         
    
    public static void Set(int argKey, List argDataList){
        synchronized (JEDBManager.dataLock){
            if(pIdx == null){
                System.out.println("set: pIdx == null");
                return;
            }

            PageEntity en = pIdx.get(String.valueOf(argKey));
            if(en == null){
                en = new PageEntity(argKey, argDataList);
            }
            else{
                en.dataList = argDataList;
            }
            
            pIdx.put(en); 
        }
    }
    
    public static void Add(int argKey, Long argHash, String argInfo){
        synchronized (JEDBManager.dataLock){
            if(pIdx == null){
                System.out.println("set: pIdx == null");
                return;
            }

            PageEntity en = pIdx.get(String.valueOf(argKey));
            if(en == null){
                List dataList = null;

                if(argHash != null){
                    dataList = new ArrayList<Integer>();
                }
                if(argInfo != null){
                    dataList = new ArrayList<PageData>();
                }
                
                en = new PageEntity(argKey, dataList);
            }
            
            if(argInfo != null){
                en.dataList.add(PageData.NewInst(argHash, argInfo));
            }
            else if(argHash != null){            
                en.dataList.add(argHash);
            }

            pIdx.put(en); 
        }
    }
    public static void Delete(int argKey){
        synchronized (JEDBManager.dataLock){
            pIdx.delete(String.valueOf(argKey));
        }
    }
    
    public static PageEntity Get(int argKey){
        synchronized (JEDBManager.dataLock){
            if(pIdx == null){
                System.out.println("get: pIdx == null");
                return null;
            }
    
            return pIdx.get(String.valueOf(argKey));
        }
    }
    
    // Close our environment and store.
    public static void Stop() {
        synchronized (JEDBManager.dataLock){
            System.out.println("Stop BDB");
            store.close();
            envmnt.close();
            pIdx = null;
        }
    } 
    
    // ʹ���߱���ʹ�� dataLcok��
    public static EntityCursor<PageEntity> GetCursor(){
        if(pIdx == null){
            System.out.println("EnumEntity: pIdx == null");
            return null;
        }
        
        // ��ȡ������(�α�)
        return  pIdx.entities();
    }
    
    public static void Test(){
        JEDBManager.Restart(folderPath,100*1024*1024);

        Date date = new Date();

        String url = "www.fangjia.com";
        ArrayList<Integer> inList = new ArrayList<Integer>();
        inList.add(100000);
        inList.add(200000);
        inList.add(300000);
        ArrayList<PageData> enList = new ArrayList<PageData>();
        PageData data = new PageData();
        enList.add(data); //new PageData(1, "www.fangjia.com")
        for(int i=0; i < 1000; i++){
            JEDBManager.Set(i, enList);
            PageEntity pageEn =  JEDBManager.Get(i);
            Common.Println("size:" + pageEn.dataList.size());
            
        }

        JEDBManager.Stop();
    }
    
    private static void TestEnum(){
        Common.Println("TestEnum");
        JEDBManager.Restart("F:/Dup_exist_html",100*1024*1024);
        
        try {
            FileWriter f = new FileWriter("F:/linkList.txt");
            EntityCursor<PageEntity> cursor = JEDBManager.GetCursor();
            for(PageEntity page: cursor){
                //Common.Println("page.URL:" + page.key);
                for(Object obj: page.dataList){
                    PageData data = (PageData)obj;
                    System.out.println(data.info);
                    f.write(data.info + "\r\n");
                }
            }
            
            f.close();
            cursor.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
//        Date date = new Date();
//        date = Common.ChangeDate(date, -1);
//        JEDBManager.Set(url, 1, "", date);
//        JEDBManager.Restart(dataSource);
        
        
        //JEDBManager.Restart(folderPath);
    }
    
    public static void main(String[] args){
        //Test();
        TestEnum();
    }
}
