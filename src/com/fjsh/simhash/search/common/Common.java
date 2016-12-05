package com.fjsh.simhash.search.common;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Common {
    
    static int Code_Default = -2;
    static int Code_Ignore  = -3;
    static int Code_Surffix = -1;
    static int Code_Word = -4;
    
    private static MessageDigest md5 = null;
    
    private static char hexDigits[] = {
        '0', '1', '2', '3',
        '4', '5', '6', '7', 
        '8', '9','a', 'b',
        'c', 'd', 'e', 'f' }; 
    
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    

    public static boolean IsCJK(char ch){
        return ch>='\u4e00' && ch <='\u9fff';
    }

    public static boolean HasCJK(String str){
        int k=0;
        
        for(; k < str.length(); k++){
            char ch = str.charAt(k);
            if(Common.IsCJK(ch) ){
                return true;
            }
        }

        return false;
    }


    public static List<String> GetMatchesSB(StringBuilder content, String prefix, String surfix){
        List<String> match_list = new ArrayList<String>();
        
        if (content == null){
            return match_list;
        }
        
        int  begin_index = content.indexOf(prefix); 
        if (begin_index > -1){
            begin_index += prefix.length();
        }
        int end_index = content.indexOf(surfix, begin_index);
        
        while (end_index >= begin_index &&  begin_index > -1){
            match_list.add(content.substring(begin_index, end_index ));
            
            begin_index = content.indexOf(prefix, end_index);
            if (begin_index > -1){
                begin_index += prefix.length();
            }
            end_index = content.indexOf(surfix, begin_index);
        }
    
        return match_list;
    }

    
    public static List<String> GetMatches(String content, String prefix, String surfix){
        List<String> match_list = new ArrayList<String>();
        
        if (content == null){
            return match_list;
        }
        
        int  begin_index = content.indexOf(prefix); 
        if (begin_index > -1){
            begin_index += prefix.length();
        }
        int end_index = content.indexOf(surfix, begin_index);
        
        while (end_index >= begin_index &&  begin_index > -1){
            match_list.add(content.substring(begin_index, end_index ));
            
            begin_index = content.indexOf(prefix, end_index);
            if (begin_index > -1){
                begin_index += prefix.length();
            }
            end_index = content.indexOf(surfix, begin_index);
        }
    
        return match_list;
    }
    
    public static String GetMatchSB(StringBuilder content, String prefix, String surfix){
        if (content == null){
            return null;
        }        
        int begin_index = content.indexOf(prefix) ;
        if (begin_index > -1)
            begin_index += prefix.length();
        int end_index = content.indexOf(surfix, begin_index);

        if (end_index > begin_index && begin_index > -1)
            return content.substring(begin_index,  end_index);
        else
            return "";
    }

    public static String GetMatch(String content, String prefix, String surfix){
        if (content == null){
            return null;
        }        
        int begin_index = content.indexOf(prefix) ;
        if (begin_index > -1)
            begin_index += prefix.length();
        int end_index = content.indexOf(surfix, begin_index);

        if (end_index > begin_index && begin_index > -1)
            return content.substring(begin_index,  end_index);
        else
            return "";
    }

    
    public static synchronized String GetMD5(StringBuilder sb){
        String md5Str = null;

        if(md5 != null && sb != null){
            md5.reset();
            md5.update(sb.toString().getBytes());
            
            try { 
                    byte[] md = md5.digest(); 
                    int j = md.length; 
                    char str[] = new char[j * 2]; 
                    int k = 0; 
                    byte b;
                    for (int i = 0; i < j; i++) { 
                         b = md[i]; 
                         str[k++] = hexDigits[b >> 4 & 0xf]; 
                         str[k++] = hexDigits[b & 0xf]; 
                    } 
                    md5Str= new String(str);

               } catch (Exception e) {
                   return null;
               } 

        }

        return md5Str;
    }
    
    public static long bytes2long(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        long res = 0;
        for (int i = 0; i < 8; i++) {
         res <<= 8;
         temp = b[i] & mask;
         res |= temp;
        }
        return res;
     }
    
    public static synchronized long GetMD5Long(StringBuilder sb){
        long md5Ret = 0;
        
        if(md5 != null && sb != null){
            md5.reset();
            md5.update(sb.toString().getBytes());
            byte[] bytes = md5.digest();
            int mask = 0xff;
            int temp = 0;
            
            for (int i = 0; i < 8; i++) {
                md5Ret <<= 8;
                temp = bytes[i] & mask;
                md5Ret |= temp;

            }
        }

//        Common.Println(Long.toBinaryString(md5Ret));
        return md5Ret;
    }    
//  byte[] byteArray = md5.digest();   
//  
//  StringBuffer md5StrBuff = new StringBuffer();   
//  for (int i = 0; i < byteArray.length; i++) {               
//      if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)   
//          md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));   
//      else   
//          md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));   
//  }
//
//  return md5StrBuff.toString();
    
    
    public static Date ChangeDate(Date date,double days)   
    {   
        return new Date(date.getTime() + (long) ( days * 24 * 60 * 60 * 1000));   
    }
    
    public static String GetClassPath()
    {
            String strClassName = Common.class.getName();
            String strClassFileName = strClassName.substring(strClassName.lastIndexOf(".") + 1, strClassName.length());
            URL url = null;
            url = Common.class.getResource(strClassFileName + ".class");
            String strURL = url.toString();
            strURL = strURL.substring(strURL.indexOf('/') + 1);
            strURL = strURL.replace("common/" + strClassFileName + ".class", "");
            int indexJar = strURL.indexOf(".jar");
            if(indexJar > -1){
                strURL = strURL.substring(0, indexJar);
            }
            int indexSplitBefore = strURL.lastIndexOf("/", indexJar);
            if(indexSplitBefore > -1){
                strURL = strURL.substring(0, indexSplitBefore);
            }
            strURL = strURL.replace("%20", " ");
            strURL = strURL.replace('\\', '/');
            
            try {
                String temp = java.net.URLDecoder.decode(strURL, "utf-8");
                strURL = temp;
            } 
            catch (Exception e) {
                 e.printStackTrace();
            }
            
            if(strURL.charAt(strURL.length() -1) == '/')
            {
                strURL = strURL.substring(0, strURL.length() -1);
            }

            if (strURL.indexOf(":")> 0)
                return strURL;
            else
                return "/" + strURL;
     }

    public static void Println(String info){
        System.out.println("[" + info + "]");
    }
    
    public static void PrintInfoTime(String info){
        long timeNumber= System.currentTimeMillis();
        Time time = new Time(timeNumber);  
        System.out.println("[" + timeNumber + "##"+ time.toString() + "##" + info+ "]"); 
    }
    
    public static List<String> ReadLinesEx(String filePath, String fileCoding) { 
        ArrayList<String> lineList = new ArrayList<String>();
        try { 
            FileInputStream fis = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, fileCoding));
            String line = null;
             while (( line = br.readLine()) != null)
             {
                 line = line.trim();
                 if(line.length() > 0){
                     lineList.add(line);
                 }
             }
             fis.close();
             br.close();
        } catch (Exception e) {
            System.out.println("failed in reading file:" + filePath);
        } 
        
        return lineList; 
    } 
    
    public static String ReadFileEx(String filePath, String fileCoding)
    {
        StringBuffer buffer = new StringBuffer(); 
   
        try 
        {
            File configFile = new File(filePath); 
            if (configFile.canRead() == true)
            {
                FileInputStream fis = new FileInputStream(filePath );
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, fileCoding));
                String line = null;
                 while (( line = br.readLine()) != null)
                 {
                     buffer.append(line);
                     buffer.append("\n");                 //   ��ӻ��з� 
                 }
                 fis.close();
                 br.close();
            }
            else
            {
                System.out.println("file not found." + filePath);
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        return buffer.toString();    
    }
    
    public static long SetBit(long src, long dst, int srcOffset, int dstOffset){
        long ret = 0;
        //long srcBit = (1L<<(srcOffset - 1)) & src;
        long srcBit = (1L<<(srcOffset - 1)) & src;
        //System.out.println("1<<(srcOffset - 1):" +Long.toBinaryString(1L<<(srcOffset - 1)) );
        //System.out.println("srcBit:" +Long.toBinaryString(srcBit) );

        long dstBit = 0;
        if(dstOffset > srcOffset){
            dstBit = srcBit<<(dstOffset - srcOffset);
        }
        else if(dstOffset < srcOffset){
            dstBit = srcBit>>(srcOffset - dstOffset);
        }
        else{
            dstBit = srcBit;
        }
        
        //System.out.println("srcBit:" +Long.toBinaryString(srcBit)+ "\tdstBit:" +Long.toBinaryString(dstBit) );
        ret = dstBit | dst;
        //System.out.println("set1:"+(dstBit!=0)+"\t"+srcOffset+"\t"+dstOffset+"\t"+Long.toBinaryString(ret));
        
        return ret;
    }
    
    public static long SetBit(long src, int srcOffset){
        long ret = 0;
        long srcBit = (1L<<(srcOffset - 1));
        //System.out.println("dstBit:" +Long.toBinaryString(dstBit) );
        ret = srcBit|src;

        return ret;
    }
    
    public static void Test1(){
        StringBuilder sb = new StringBuilder("www.fangjia.com");
        long md5Ret = GetMD5Long(sb);
        System.out.println(md5Ret);
    }
    
    public static void main(String[] args){
        //Test1();
        
        long src = 0xff;
        long dst = 100000;

        System.out.println(Long.toBinaryString(src));
        System.out.println(Long.toBinaryString(dst));

        System.out.println(Long.toBinaryString(SetBit(src, dst, 8, 4)));
    }
}
