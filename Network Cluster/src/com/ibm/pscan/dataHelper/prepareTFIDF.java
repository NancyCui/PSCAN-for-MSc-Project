package com.ibm.pscan.dataHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.ibm.pscan.util.IOPath;

/**
 * Prepare the documents, and words need to be count
 *
 * @author Ningxin
 */

public class prepareTFIDF {
	 /**
	  * Remove the dupicate item in an arraylist
	  */
	public static ArrayList<String> removeDuplicate(ArrayList<String> arlList)  
	  {  
		HashSet<String> h = new HashSet<String>(arlList);
		arlList.clear();
		arlList.addAll(h);	 
		return arlList;
	  }  
	
	/**
	 * For each hubs, write the related word into a hash map
	 */
	public static Map<String, ArrayList<ArrayList<String>>> writeIntoMap(ArrayList<ArrayList<String>> messages, ArrayList<String> hubs) {
		Pattern pattern = Pattern.compile("([#,$]*[0-9]*([th*]|[pm]*))");   
		Map<String, ArrayList<ArrayList<String>>> memberMessage= new HashMap<String, ArrayList<ArrayList<String>>>();
		String[] commons=IOPath.COMMON_WORDS;
		ArrayList<String> commonWords = new ArrayList<String>(Arrays.asList(commons));
		for(ArrayList<String> m: messages){
			if(hubs.contains(m.get(0))&&(!m.get(1).equals(""))){
				String clusterID=m.get(0);
				String message=m.get(1);
				StringTokenizer tokenizer = new StringTokenizer(message, " \t\n\r\f,.:;?![]/()'");
				if(memberMessage.containsKey(clusterID)){
					ArrayList<ArrayList<String>> eachMessage=memberMessage.get(clusterID);
					ArrayList<String> word=new ArrayList<String>();
					while (tokenizer.hasMoreTokens()) {
						String w=tokenizer.nextToken().toLowerCase().replace("\"", "").trim();
						if(!commonWords.contains(w)&&(!w.equals(""))&&(!pattern.matcher(w).matches())){
							word.add(w);
						}						
				    }
					Collections.sort(word);
					eachMessage.add(word);		
					memberMessage.put(clusterID,eachMessage);
				}
				else{
					ArrayList<ArrayList<String>> eachMessage=new ArrayList<ArrayList<String>>();
					ArrayList<String> word=new ArrayList<String>();
					while (tokenizer.hasMoreTokens()) {
						String w=tokenizer.nextToken().toLowerCase();
						if(!commonWords.contains(w)){
							word.add(w);
						}						
				    }
					Collections.sort(word);
					eachMessage.add(word);		
					memberMessage.put(clusterID,eachMessage);
				}
			}		
		}
		return memberMessage;		
	}
	
	/**
	 * Get the words
	 */
	public static ArrayList<String> getAllWords(
		Map<String, ArrayList<ArrayList<String>>> message) {
		//all words needed to be calculated
		ArrayList<String> allWords=new ArrayList<String>();
			
		Iterator<String> keys = message.keySet().iterator();
	 	while(keys.hasNext()){
	 		String key=keys.next().toString();			
	 		ArrayList<ArrayList<String>> eachMemberMessage=message.get(key);
	 		for(ArrayList<String> e: eachMemberMessage){
	 			for(String w: e){
	 				allWords.add(w);
	 			}
	 		}
	 	}	
	 	Collections.sort(allWords);
	 	ArrayList<String> words=prepareTFIDF.removeDuplicate(allWords);	 	
		return words;
	}

	/**
	 * Prepare for TD-IDF document and words
	 */
	public static ArrayList<ArrayList<String>> formatedAllDocument(
			Map<String, ArrayList<ArrayList<String>>> allDocuments) {
		
		Iterator<String> keys = allDocuments.keySet().iterator();
		ArrayList<ArrayList<ArrayList<String>>> all=new ArrayList<ArrayList<ArrayList<String>>>();
	 	while(keys.hasNext()){
	 		String key=keys.next().toString();			
	 		ArrayList<ArrayList<String>> d=allDocuments.get(key);
	 		all.add(d); 		
	 	}	
	 	ArrayList<ArrayList<String>> allFDoc=new ArrayList<ArrayList<String>>();
	 	
	 	for(int i=0;i<all.size();i++){
	 		ArrayList<ArrayList<String>> document=all.get(i);
	 		ArrayList<String> formatedDocument=formatDocument(document);
	 		allFDoc.add(formatedDocument);	 		
	 	}
		return allFDoc;
	}
	
	/**
	 * Get all words in each document
	 */
	private static ArrayList<String> formatDocument(
			ArrayList<ArrayList<String>> document) {
		ArrayList<String> formatedDocument=new ArrayList<String>();
		for(ArrayList<String> d:document){
			for(String w:d){
				formatedDocument.add(w);
			}
		}
		Collections.sort(formatedDocument);
		return formatedDocument;
	}
}
