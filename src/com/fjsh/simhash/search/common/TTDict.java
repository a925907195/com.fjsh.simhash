package com.fjsh.simhash.search.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
*@Title:TTDict
*TODO:
*@Description: �ֵ���
*@author:fujiansheng@58ganji.com
*@date 2016��11��28�� ����2:27:57
*
*/
public class TTDict<T> {

	public TNode<T> tree = new TNode<T>();
	public Map<Character, List<TNode<T>>> charNodeMap = null; 

	public int nodeCount = 0;
    
    public TTDict(){
        tree.childList = new ArrayList<TNode<T>>();
        charNodeMap = new HashMap<Character, List<TNode<T>>>();
    }
    
    public int GetSize(){
        return nodeCount;
    }
    
    public void Clean(){
    	if(tree.childList != null){
    		tree.childList.clear();
    	}
        charNodeMap.clear();
        tree.childList = new ArrayList<TNode<T>>();
        charNodeMap = new HashMap<Character, List<TNode<T>>>();
        nodeCount = 0;
    }
    
    public void SetValue( String key, T value, int[] codeArray){
        List<TNode<T>> curNodeList = null;
        
        int lenKey = key.length();

        if(lenKey == 0){
            return;
        }
        
        TNode<T> dstNode = this.tree;
        if (this.tree.childList != null)
        	curNodeList = this.tree.childList;

        // �ҵ����ڵ����Ӧ�ڵ㡣
        int index = 0;
        boolean exist = false;
        while (curNodeList!= null){
            TNode<T> node = BSearch(key.charAt(index), curNodeList);
            
            curNodeList = null;
            if (node != null){
                dstNode = node;
                // match a char.
            	curNodeList =node.childList;
                
                if (index == lenKey -1){
                	exist = true;
                    break;
                }
                else{
                	index+= 1;
                }
            }
            else
            {
            	break;
            }
        }
        
        if (exist == true){
            // Update dict value.
            dstNode.value = value;
        }
        else{
            nodeCount++;

            // Add to dict value.
            while (index < lenKey){
                TNode<T> node = new TNode<T>();
                node.ch = key.charAt(index);
                if (index == lenKey -1){
                    node.value = value;
                }
            
                if (dstNode.childList == null){
                    dstNode.childList = new ArrayList<TNode<T>>(1);
                }
                // ˳�����
                int lenChild = dstNode.childList.size();
                if (lenChild > 0){
                    // TODO: b-search.
                    int insertIndex = 0;
                    for(TNode<T> curNode: dstNode.childList){
                        if(curNode.ch > node.ch){
                            insertIndex +=1;
                        }
                        else{
                            break;
                        }
                    }
                    if(insertIndex < lenChild){
                        dstNode.childList.add(insertIndex, node);
                        
                        if(codeArray[index] == Common.Code_Default){
                        	AddIndex(node);
                        }
                    }
                    else{
                        dstNode.childList.add(node);
                        if(codeArray[index] == Common.Code_Default){
                        	AddIndex(node);
                        }
                    }
                }
                else{
                    dstNode.childList.add(node);
                    if(codeArray[index] == Common.Code_Default){
                    	AddIndex(node);
                    }
                }

                dstNode = node;
                index+= 1;
            }
        }
    }
    
    public void SetValue( String key, T value){
        List<TNode<T>> curNodeList = null;
        
        int lenKey = key.length();

        if(lenKey == 0){
            return;
        }
        
        TNode<T> dstNode = this.tree;
        if (this.tree.childList != null)
        	curNodeList = this.tree.childList;

        // �ҵ����ڵ����Ӧ�ڵ㡣
        int index = 0;
        boolean exist = false;
        while (curNodeList!= null){
            TNode<T> node = BSearch(key.charAt(index), curNodeList);
            
            curNodeList = null;
            if (node != null){
                dstNode = node;
                // match a char.
            	curNodeList =node.childList;
                
                if (index == lenKey -1){
                	exist = true;
                    break;
                }
                else{
                	index+= 1;
                }
            }
            else
            {
            	break;
            }
        }
        
        if (exist == true){
            // Update dict value.
            dstNode.value = value;
        }
        else{
            nodeCount++;

            // Add to dict value.
            while (index < lenKey){
                TNode<T> node = new TNode<T>();
                node.ch = key.charAt(index);
                if (index == lenKey -1){
                    node.value = value;
                }
            
                if (dstNode.childList == null){
                    dstNode.childList = new ArrayList<TNode<T>>(1);
                }
                // ˳�����
                int lenChild = dstNode.childList.size();
                if (lenChild > 0){
                    // TODO: b-search.
                    int insertIndex = 0;
                    for(TNode<T> curNode: dstNode.childList){
                        if(curNode.ch > node.ch){
                            insertIndex +=1;
                        }
                        else{
                            break;
                        }
                    }
                    if(insertIndex < lenChild){
//                        System.out.println("insertIndex=" + insertIndex);
                        dstNode.childList.add(insertIndex, node);
                        AddIndex(node);
                        //dstNode.childList.add(node);
                    }
                    else{
                        dstNode.childList.add(node);
                        AddIndex(node);
                    }
                    
                }
                else{
                    dstNode.childList.add(node);
                    AddIndex(node);
                }

                dstNode = node;
                index+= 1;
            }
        }
    }
    
    private void AddIndex(TNode<T> node){
        List<TNode<T>> nodeList = charNodeMap.get(node.ch);
        if(nodeList == null){
            nodeList = new ArrayList<TNode<T>>();
            charNodeMap.put(node.ch, nodeList );
        }

        nodeList.add(node);
    }
    
    public void PrintTree(){
    	
    	PrintNode(this.tree);
    }
    
    public void PrintNode(TNode<T> node){
    	if(node.childList != null){
	    	for(TNode<T> sub : node.childList){
	    		System.out.println("[" + node.ch+ "=>" +sub.ch + (sub.value == null)+"]");
	    		PrintNode(sub);
	    	}
    	}
    }

    public T GetValue(String key){
        TNode<T> dstNode = GetValueByRoot(key, this.tree);
        
        T value = null;
        if(dstNode != null){
            value = dstNode.value;
        }
        return value;
    }
    
    private TNode<T> GetValueByRoot(String key, TNode<T> parentNode){
        int index = 0 ;
        int lenKey = key.length();
        if(lenKey == 0){
            return null;
        }
        
        // Search dict.
        TNode<T> dstNode = null;
        while (index < lenKey){
            dstNode = BSearch(key.charAt(index), parentNode.childList);
            if (dstNode != null){
                parentNode = dstNode;
                index += 1;
            }
            else
                break;
        }
            
        if (index == lenKey){
            return dstNode;
        }
        else{
            return null;
        }
    }
   
    public List<T> SubGetValues(String key, int countLimit){
        List<T> valueList = new ArrayList<T>();
        Map<T, Boolean> valueMap = new HashMap<T, Boolean>();
        
        if(key.length() == 0){
            return valueList;
        }
        
        // TODO: ���Ƶ�Ƶ�ʡ�
        char firstCH = key.charAt(0);
        String restKey = key.substring(1);
        List<TNode<T>> nodeList = charNodeMap.get(firstCH);
        if(nodeList != null){
            if(restKey.length() == 0){
                for(TNode<T> node: nodeList){
                    if(node.value != null){
                        valueMap.put(node.value, true);
                    }
                    GetSubNode(node, valueMap, countLimit);
                }
            }
            else{
            	
            	List<TNode<T>> subNodeList = new LinkedList<TNode<T>>(); 
                for(TNode<T> node: nodeList){
                    TNode<T> nodeDir = GetValueByRoot(restKey, node);
                    
                    if(nodeDir != null){
                        if(valueMap.size() >= countLimit){
                        	break;
                        }
                        // Get sub nodes.
                        if(nodeDir.value != null){
                            valueMap.put(nodeDir.value, true);
                        }
                        subNodeList.add(nodeDir);

                    }
                }
                
                for(TNode<T> nodeDir: subNodeList){
                    if(valueMap.size() >= countLimit){
                    	break;
                    }
                    GetSubNode(nodeDir, valueMap, countLimit);
                }
            }
        }
        
        // ȥ�ء�
        for(Iterator<T> it = valueMap.keySet().iterator(); it.hasNext();){
            T value = it.next();
            valueList.add(value);
        }

        return valueList;
    }
    
    public void GetSubNode(TNode<T> node, Map<T, Boolean> valueMap, int countLimit){
        if(node.childList != null){
        	List<TNode<T>> subNodeList = new LinkedList<TNode<T>>(); 
            //������ȱ���
            for(TNode<T> sub : node.childList){
                if(sub.value != null){
                    if(valueMap.size() >= countLimit){
                    	return;
                    }
                    
                    valueMap.put(sub.value, true);
                }
                subNodeList.add(sub);
            }
            
            for(TNode<T> sub: subNodeList){
                if(valueMap.size() >= countLimit){
                	return;
                }

                GetSubNode(sub, valueMap, countLimit);
            }
        }
    }
    
    public TNode<T> NextNode(char nextChar, TNode<T> curNode ){
        if(curNode == null){
            curNode = this.tree;
        }
        return BSearch(nextChar, curNode.childList);
    }

    /**
     * ���ֲ���
     * @param ch
     * @param nodeList
     * @return
     */
    private TNode<T> BSearch(char ch, List<TNode<T>> nodeList){
        TNode<T> dstNode = null;
        if (nodeList != null){
            int lenNode = nodeList.size();
            
            if (lenNode > 0){
                int start = 0;
                int end = lenNode -1;
                
                while (end>=start){ 
                    int mid=start+((end-start)/2);
                    if (nodeList.get(mid).ch > ch){
                        start = mid + 1;
                    }
                    else if (nodeList.get(mid).ch < ch){
                        end = mid -1;
                    }
                    else{
                        dstNode = nodeList.get(mid);
                        break;
                    }
                }
            }
        }

        return dstNode;
    }

}
