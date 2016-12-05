package com.fjsh.simhash.search.common;

import java.util.ArrayList;
import java.util.List;


public class MMWordSplitter {
    
    TTDict dict = null;
    public MMWordSplitter(TTDict argDict){
        dict = argDict;
    }

    /**
     * ���ı������з֣���ǰ��ƥ��
     * @param text
     * @return
     */
    public List<String> Split(String text){
        List<String> wordList = new ArrayList<String>();
        //
        // ����tire tree �ʵ�ṹ��һ�α����ı���ɷִ�
        // ������ƥ��
        //
        if (text == null){
            return wordList;
        }
        
        int i = 0;
        int textLen = text.length();
        while (i < textLen){
            int wordStart = i;
            char ch = text.charAt(i);
            TNode node = this.dict.NextNode(ch, null);
            
            i += 1;
            int leafId = -1; // tire tree leaf Ϊ��
            while (i < (textLen) && node != null){
                ch = text.charAt(i);
                node = this.dict.NextNode(ch, node);
                if (node != null){
                    i += 1;
                    // if is leaf of tire tree
                    if (node.value != null) 
                        leafId = i;
                }
            }
            
            String word = null;
            
            if (leafId != -1){
                word = text.substring(wordStart,leafId);
                //word = word.trim();
                wordList.add(word);
                i = leafId;
            }
            else{
                word = text.substring(wordStart,wordStart+1);
                //word = word.trim();
                wordList.add(word);
                i = wordStart + 1;
            }
        }
            
        return wordList;
    }
    
    public int[] SplitAsCode(String text){
        int[] codeArray = new int[text.length()]; 
        for(int i=0; i <text.length(); i++ ){
            codeArray[i] = Common.Code_Default; // Not set.
        }
        
        //
        // ����tire tree �ʵ�ṹ��һ�α����ı���ɷִ�
        // ������ƥ��
        //
        if (text == null){
            return codeArray;
        }
        
        int i = 0;
        int textLen = text.length();
        while (i < textLen){
            int wordStart = i;
            char ch = text.charAt(i);
            TNode node = this.dict.NextNode(ch, null);
            
            i += 1;
            int leafId = -1; // tire tree leaf Ϊ��
            while (i < (textLen) && node != null){
                ch = text.charAt(i);
                node = this.dict.NextNode(ch, node);
                if (node != null){
                    i += 1;
                    // if is leaf of tire tree
                    if (node.value != null) 
                        leafId = i;
                }
            }
                        
            if (leafId != -1){
                for(int k=wordStart; k < leafId; k ++){
                    codeArray[k] = Common.Code_Word;
                }
                i = leafId;
            }
            else{
                codeArray[wordStart] = Common.Code_Default;
                i = wordStart + 1;
            }
        }
            
        return codeArray;
    }
    
    private static void TestWordSplit(){
        
        TTDict<Integer> dict = new TTDict<Integer>();
        dict.SetValue("�Ϻ���1��", 1);
        dict.SetValue("�Ϻ���", 1);
        dict.SetValue("1��", 1);
        dict.SetValue("�Ϻ�", 1);
        dict.SetValue("�Ϻ���2��", 1);
        dict.SetValue("�Ϻ���3��", 1);
        dict.SetValue("�Ϻ���4��", 1);
        MMWordSplitter splitter = new MMWordSplitter(dict);
        System.out.println("===========");
        List<String> wordList = splitter.Split("�Ϻ���1���Ϻ���2���Ϻ�3���Ϻ���4��");
        for(String word: wordList){
            System.out.println(word);
        }

        System.out.println("===========");
        int[] codeArray = splitter.SplitAsCode("�Ϻ���1���Ϻ���2���Ϻ�3���Ϻ���4��");
        for(int code: codeArray){
            System.out.println(code);
        }
        
        //MMWordSplitter
    }
    public static void main(String[] args){
        TestWordSplit();
    }
}

