package com.fjsh.simhash.search.dupcheck.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fjsh.simhash.search.common.Common;
import com.fjsh.simhash.search.common.VectorAdapter;

/**
*@Title:SimHashAdapter
*TODO:
*@Description: 进行simhash的相似度计算
*@author:fujiansheng@58ganji.com
*@date 2016年12月1日 下午8:45:44
*
*/
public class SimHashAdapter {

    //
    public static int hashbits = 64;

    /**获取simhash值
     * @param featureMap
     * @return
     */
    public static long GetSimiHash(Map<String, Double> featureMap){
        
        //        1，将一个f维的向量V初始化为0；f位的二进制数S初始化为0；
        double[] V = new double[hashbits];
        
        long S  = 0;
        
        //        2，对每一个特征：用传统的hash算法对该特征产生一个f位的签名b。对i=1到f：
        //        如果b的第i位为1，则V的第i个元素加上该特征的权重；
        //        否则，V的第i个元素减去该特征的权重。
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<String, Double>> it = featureMap.entrySet().iterator();
        while(it.hasNext()){
            Entry<String, Double> en = it.next();
            sb = new StringBuilder(en.getKey());
            double weight = en.getValue();
            Long md5 = Common.GetMD5Long(sb);
            long bitmask = 0;
            //这里策略是通过bitmask不断地左移，然后跟当前数值做与运算，如果大于0则此位置上的二进制位1否则不是1
            for (int i=0; i < hashbits; i++){
                bitmask = 1L << i;
                if ((md5 & bitmask) > 0){
                    V[i] += weight; //查看当前bit位是否为1，是的话则将该位+weight
                }
                else{
                    V[i] -= weight; //否则得话，该位减weight
                }
            }
        }
        
        //        3. 如果V的第i个元素大于0，则S的第i位为1，否则为0；
        long bitmask = 0;
        for(int i=0; i < V.length; i++){
            if(V[i] > 0){
                bitmask = 1L << i;
                S |= bitmask; 
//                Common.Println(Long.toBinaryString(S));
            }
        }

        //        4，输出S作为签名。  
        return S;
    }
    
    
    
    private static void TestFeatureHash(){
        Map<String, Double> featureAMap = new HashMap<String, Double>();
        featureAMap.put("第1", 1D);
        featureAMap.put("第2", 1D);
        featureAMap.put("第3", 1D);
        featureAMap.put("第4", 1D);
        featureAMap.put("第5", 1D);
        featureAMap.put("第6", 1D);
        featureAMap.put("第7", 1D);
        featureAMap.put("第8", 1D);
        
        Map<String, Double> featureBMap = new HashMap<String, Double>();
        featureBMap.put("第1", 1D);
        featureBMap.put("第2", 1D);
        featureBMap.put("第3", 1D);
        featureBMap.put("第4", 1D);
        featureBMap.put("第5", 1D);
        featureBMap.put("第6", 2D);
        //featureBMap.put("第7", 1D);
        featureBMap.put("第8", 1D);
        
        long simHashA = GetSimiHash(featureAMap);
        long simHashB = GetSimiHash(featureBMap);
        Common.Println("simHashA:" + Long.toBinaryString( simHashA));
        Common.Println("simHashB:" +Long.toBinaryString(simHashB));
    }
    

    private static void TestStringHash(){
        VectorAdapter vectorAdapter = new VectorAdapter(); 
        List<String> stopWordPathList = new ArrayList<String>();
        List<String> wordPathList = new ArrayList<String>();
        String weightPath = "./Dup_Config/IDF.txt";
        wordPathList.add("./Dup_Config/fullzonelist.txt");
        wordPathList.add("./Dup_Config/Districtlist.txt");
        wordPathList.add("./Dup_Config/ChineseWords.txt");
        stopWordPathList.add("./Dup_Config/stopwords.txt");
        stopWordPathList.add("./Dup_Config/stopwords2.txt");
        stopWordPathList.add("./Dup_Config/stopwords_en_cn.txt");
        vectorAdapter.Init(wordPathList, stopWordPathList, weightPath, 100000);
        
        Map<String, Double> featureAMap = vectorAdapter.GetVector("冒着敌人地炮火前进");
        Map<String, Double> featureBMap = vectorAdapter.GetVector("敌人冒着地炮火前进");
        //获取simhash值
        long simHashA = GetSimiHash(featureAMap);
        long simHashB = GetSimiHash(featureBMap);
        Common.Println("simHashA:" + Long.toBinaryString( simHashA));
        Common.Println("simHashB:" +Long.toBinaryString(simHashB));
        Common.Println("HammingDistance:" + HashDataAdapter.GetHammingDistance(simHashA, simHashB));
        
    }
    
    
    /**
     * 处理流程，先加载词库，包括基本词库，停用词词库，词频，然后基于前向匹配进行分词得到词的基本权重，计算词的simhash值，
     * 然后对数据进行异或处理后计算1的个数
     * @param args
     */
    public static void main(String[] args){
        TestStringHash();
    }
}
