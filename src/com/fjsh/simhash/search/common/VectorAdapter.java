package com.fjsh.simhash.search.common;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VectorAdapter {
    
    MMWordSplitter splitter = null;
    Map<String ,Boolean> stopWordMap = new HashMap<String ,Boolean>();
    Map<String ,Double> weightMap = new HashMap<String ,Double>();
    int DOC_COUNT = 100000;
    
    public void Init(List<String> wordPathList, List<String> stopWordPathList, String weightPath, int docCount){
    	DOC_COUNT = docCount;
    	//将词存放在字典树
    	InitSplitter(wordPathList);
        //将词简单存放到hashmap中0
        InitStopWord(stopWordPathList);
        //将词简单存放到hashmap中，key为词，value为idf值，原始文档存储的是词的文档频率
        InitWeight(weightPath);
        
    }
    
    /**
     * 将词放入词典树中
     * @param wordPathList
     */
    private void InitSplitter(List<String> wordPathList){
        TTDict<Boolean> dict = new TTDict<Boolean>();
        
        for(String filePath: wordPathList){
            List<String> lines= Common.ReadLinesEx(filePath, "utf-8");
            Common.Println("VectorAdapter:InitSplitter with word:"+filePath+"  " +lines.size());            
            for(String line : lines){
                line = line.trim().toLowerCase();
                if(line.isEmpty() == false){
                    dict.SetValue(line, true);
                }
            }
        }

        splitter = new MMWordSplitter(dict);
        Common.Println("VectorAdapter:InitSplitter with word:" +dict.GetSize());

    }
    
    private void InitStopWord(List<String> stopWordPathList){
        for(String filePath: stopWordPathList){
            List<String> lines = Common.ReadLinesEx(filePath, "utf-8");
            for(String line : lines){
                line = line.trim().toLowerCase();
                if(line.isEmpty() == false){
                    stopWordMap.put(line, true);
                }
            }
        }
    }
    
    private void InitWeight(String weightPath){
        List<String> lines= Common.ReadLinesEx(weightPath, "utf-8");
        for(String line : lines){
            line = line.trim().toLowerCase();
            if(line.isEmpty() == false){
                // 
                String[] parts = line.split("\t");
                if(parts.length == 2){
                    //
                    String text= parts[0].trim();
                    float DF = Float.parseFloat(parts[1]);
                    double IDF = Math.log(DOC_COUNT/DF);
                    weightMap.put(text, IDF);
                }
            }
        }
    }
    
    double GRAME_SZIE = 4;

    public Map<String, Double> GetVector(String text){
		return GetVector(text, true);
	}

    public Map<String, Double> GetVector(String text, boolean bTopicCompare){
        Map<String, Double> vector = new HashMap<String, Double>();
        // 直接使用TF,这样比较的是字符之间的相似度，而不是中心意思上的相似度。
        List<String> wordList = splitter.Split(text);
        if(bTopicCompare)
        {
            for(String word: wordList){
                Double weight = weightMap.get(word);
                if(weight == null){
                    weight = Math.log(DOC_COUNT);
                }

                if(Common.HasCJK(word)){
                    Double allWeight =  vector.get(word);
                    if(allWeight == null){
                        vector.put(word, weight);
                    }
                    else{
                        vector.put(word, weight + allWeight);
                    }
                }
            }
        }
        else
        {
            // 进行NGRAME采样,用于比较相似度。
            for(int i=0; i < wordList.size(); i++ ){
               String seq = null;
               
               if( i  +  GRAME_SZIE <=wordList.size()){
                   seq = wordList.get(i) + wordList.get(i+1)+ wordList.get(i+2);
               }
               else{
                   seq = wordList.get(i);
               }
               
               if(Common.HasCJK(seq)){
                   Double allWeight =  vector.get(seq);
                   if(allWeight == null){
                       vector.put(seq, 1.0);
                   }
                   else{
                       vector.put(seq, 1 + allWeight);
                   }
               }
           }
        	
        }
       
        
        return vector;
    }
}
