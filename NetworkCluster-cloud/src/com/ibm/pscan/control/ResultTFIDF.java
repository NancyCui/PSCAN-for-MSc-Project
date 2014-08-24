package com.ibm.pscan.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.ibm.pscan.dataHelper.GetDetails;
import com.ibm.pscan.dataHelper.prepareTFIDF;
import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.util.Config;

/**
 * Read the result from could and calculate the TFIDF value for the contents of each hub
 * Write the result to CSV file
 * 
 * @author Ningxin
 */
public class ResultTFIDF {
	
	private static String cloudFilePath="example/data/pscanOutput/result.csv";
	private static String outputFilePath=Config.BASE_PATH+"result.csv";
	private static String containerName=Config.CONTAINER_NAME;
	private static String filename=Config.BASE_PATH+"tfidf.csv";
	
	public static void main(String[] args) throws IOException {
		
		ArrayList<String> hubs=getHubs();
		ArrayList<ArrayList<String>> messages=GetDetails.messages();
		ArrayList<ArrayList<String>> users=GetDetails.userDetails();
		getUserInfo(hubs,users);
	
		//Put each member's message into a hashmap
		Map<String,ArrayList<ArrayList<String>>> allDocuments= prepareTFIDF.writeIntoMap(messages, hubs);
	 	//Get the key
		Iterator<String> keys = allDocuments.keySet().iterator();
		ArrayList<String> keySet=new ArrayList<String>();
	 	while(keys.hasNext()){
	 		String key=keys.next().toString();	
	 		keySet.add(key);
	 	}
		
		//The words need to calculate TF-IDF
		ArrayList<String> allWords=prepareTFIDF.getAllWords(allDocuments);
		Collections.sort(allWords);
		
		ArrayList<ArrayList<String>> allFDoc=prepareTFIDF.formatedAllDocument(allDocuments);
			 	
	 	for(int i=0;i<allFDoc.size();i++){
	 		System.out.println(allFDoc.get(i));
	 	}
	 	
	 	ArrayList<Integer> maxF=new ArrayList<Integer>();
	 	for(int i=0;i<allFDoc.size();i++){
			ArrayList<String> document=allFDoc.get(i);
			int maxFrequency=mostFrequencyWordInDoc(document);
			maxF.add(maxFrequency);
		}

	 	/*
	 	 * For each word, calcute realted the TFIDF value of each document
	 	 */
	 	ArrayList<ArrayList<ArrayList<String>>> result=new ArrayList<ArrayList<ArrayList<String>>>();
		for(String w: allWords){
			ArrayList<ArrayList<String>> wordTFIDF=calculateTFIDF(w,allFDoc,keySet,maxF);
			result.add(wordTFIDF);
		}		
		writeResultToFile(keySet,result,filename);	
	}
	
	/**
	 * Write result to csv file 
	 */
	private static void writeResultToFile(ArrayList<String> keySet,
			ArrayList<ArrayList<ArrayList<String>>> result, String filename2) {		
		int i=0;
		for(ArrayList<ArrayList<String>> r: result){
			ArrayList<String> content= new ArrayList<String>();
			for(int j=0;j<keySet.size();j++){
				if(i==0){
					ArrayList<String> title= new ArrayList<String>();
					for(String k:keySet){
						title.add("User:"+k+" Word");
						title.add("User:"+k+" TFIDF");
					}
					i++;
					File f=new File(filename);
					if(f.exists()){
						f.delete();
					}
					CsvFileIO.insertCSVFile(filename,title);
				}
				
				String word=r.get(j).get(1);
				String value=r.get(j).get(2);
				content.add(word);
				content.add(value);					
			}	
			CsvFileIO.insertCSVFile(filename,content); 
		}
			
		
	}

	/**
	 * Calculate the TF-IDF value for the parcular word
	 * Wrtie the value to csv file, named by each hubs name
	 * 
	 * @param word allDocuments keySet 
	 * @param maxF 
	 * @return 
	 */
	private static ArrayList<ArrayList<String>> calculateTFIDF(String word, ArrayList<ArrayList<String>> allFDoc, ArrayList<String> keySet, ArrayList<Integer> maxF) {
		ArrayList<ArrayList<String>> tfidfValue=new ArrayList<ArrayList<String>>();
		double tfidf;
		for(int i=0;i<allFDoc.size();i++){
			ArrayList<String> output=new ArrayList<String>();
			ArrayList<String> document=allFDoc.get(i);
			String key=keySet.get(i);			
			int max=maxF.get(i);
			double tf=calculateTF(word,document,max);
			double idf=calculateIDF(word, allFDoc);
			tfidf=tf*idf;
			output.add(key);
			output.add(word);
			output.add(Double.toString(tfidf));
			tfidfValue.add(output);
		}
		return tfidfValue;		
	}

	/**
	 * Calculte the IDF value for the word
	 * @return 
	 */
	private static double calculateIDF(String word,
			ArrayList<ArrayList<String>> allFDoc) {
		double idf=0.0;
		double n=allFDoc.size();
		int appear=0;
		for(ArrayList<String> d: allFDoc){
			if(d.contains(word)){
				appear++;
			}
		}
		idf=Math.log((double)(n/appear));
		return idf;
	}

	/**
	 * Calculate the TF value for the word
	 * @param max 
	 * @return 
	 */
	private static double calculateTF(String word,
			ArrayList<String> document, int max) {
		double tf=0;
		int frequency=0;
		if(document.contains(word)){
			frequency++;
		}
		tf=0.5*frequency/max+0.5;
		return tf;
	}

	/**
	 * maximum raw frequency of any term in the document
	 */
	private static int mostFrequencyWordInDoc(ArrayList<String> document) {	
		int max=0;
		int maxFrequency=0;
		for(String w: document){
			int frequency=0;
			for(String w2: document){
				if(w.equals(w2)){
					frequency++;
				}
			}
			if(maxFrequency<frequency){
				maxFrequency=frequency;
			}
			max=Math.max(maxFrequency, frequency);
		}
		return max;
	}

	/**
	 * Get the details of the hubs
	 * @param hubs
	 * @param users
	 */
	private static void getUserInfo(ArrayList<String> hubs, ArrayList<ArrayList<String>> users) {
		 for(int i=0;i<users.size();i++){
			 ArrayList<String> u=users.get(i);
			 if(!hubs.contains(u.get(0))){
				 users.remove(u);
				 i--;
			 }
		 }
		 System.out.println(users.toString());
	 }	 

	/**
	 * Get the ID of the hubs
	 */
	private static ArrayList<String> getHubs() throws IOException {
			AzureIO.downloadFromAzure(cloudFilePath,containerName, outputFilePath);
			ArrayList<ArrayList<String>> contents=CsvFileIO.readCSVFile(outputFilePath);
			ArrayList<String> hubs= new ArrayList<String>();
			for(ArrayList<String> c: contents){
				if(c.get(0).equals("Group:hubs")){
					for(int i=1;i<c.size();i++){
						hubs.add(c.get(i).trim());
					}
					
				}
			}
			return hubs;
	 }







	
}
