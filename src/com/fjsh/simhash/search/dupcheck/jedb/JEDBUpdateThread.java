package com.fjsh.simhash.search.dupcheck.jedb;


// 定期保存数据。
public class JEDBUpdateThread extends Thread {
    
    public boolean terminate = false;
    private long update_sec_interval = 3600;
    private String folderPath = "JEDB"; 
    private int cache_size= 100*1024*1024;
    public JEDBUpdateThread(long arg_update_sec_interval, String argfolderPath, int argCacheSize){
        this.update_sec_interval = arg_update_sec_interval;
        this.folderPath = argfolderPath;
        this.cache_size = argCacheSize;
    }
    
    @Override
    public void run(){
        while(terminate == false){
            JEDBManager.Restart(this.folderPath, cache_size);
            try {
                Thread.sleep(this.update_sec_interval * 1000);
            } catch (InterruptedException e) {
                
            }
        }
    }
}
