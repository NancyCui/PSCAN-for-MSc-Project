package com.ibm.pscan.control;

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
	private static String filenameAll=Config.BASE_PATH+"tfidfall.csv";
	
	public static void main(String[] args) throws IOException {
		
		//Get id of the hubs
		ArrayList<String> hubs=getHubs();
		//Get id of the outliers
//		ArrayList<String> hubs=getOutliers();
		ArrayList<ArrayList<String>> messages=GetDetails.messages();
		ArrayList<ArrayList<String>> users=GetDetails.userDetails();
		ArrayList<String> allMember=new ArrayList<String>();
		for(ArrayList<String> u : users){
			String member=u.get(0);
			allMember.add(member);
		}
				
		getUserInfo(hubs,users);
			
		//Put each member's message into a hashmap
		Map<String,ArrayList<ArrayList<String>>> allDocuments= prepareTFIDF.writeIntoMap(messages, hubs);
		Map<String,ArrayList<ArrayList<String>>> allMembersDocuments= prepareTFIDF.writeIntoMap(messages, allMember);

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
		ArrayList<ArrayList<String>> all=prepareTFIDF.formatedAllDocument(allMembersDocuments);

	 	/*
	 	 * For each word, calcute realted the TFIDF value of each document
	 	 */
	 	
	 	ArrayList<ArrayList<ArrayList<String>>> result=new ArrayList<ArrayList<ArrayList<String>>>();
	 	ArrayList<ArrayList<ArrayList<String>>> resultAll=new ArrayList<ArrayList<ArrayList<String>>>();
		for(String w: allWords){
			ArrayList<ArrayList<String>> wordTFIDF=calculateTFIDF(w,allFDoc,keySet);			
			ArrayList<ArrayList<String>> wordTFIDFAll=calculateTFIDFAll(w,allFDoc,all,keySet);
			result.add(wordTFIDF);
			resultAll.add(wordTFIDFAll);
		}		
		
		CsvFileIO.writeResultToFile(keySet,result,filename);	
		CsvFileIO.writeResultToFile(keySet,resultAll,filenameAll);	
	}
	


	
	/**
	 * Calculate the TF-IDF value for word compared to all users
	 * @param w
	 * @param allFDoc
	 * @param all
	 * @param keySet
	 * @return
	 */
	private static ArrayList<ArrayList<String>> calculateTFIDFAll(String word,
			ArrayList<ArrayList<String>> allFDoc,
			ArrayList<ArrayList<String>> all, ArrayList<String> keySet) {
		ArrayList<ArrayList<String>> tfidfValue=new ArrayList<ArrayList<String>>();
		double tfidf;
		for(int i=0;i<allFDoc.size();i++){
			ArrayList<String> output=new ArrayList<String>();
			ArrayList<String> document=allFDoc.get(i);		
			String key=keySet.get(i);			
			double tf=calculateTF(word,document);
			double idf=calculateIDF(word, all);
			tfidf=tf*idf;
			output.add(key);
			output.add(word);
			output.add(Double.toString(tfidf));
			tfidfValue.add(output);
		}
		
		return tfidfValue;	
	}

	/**
	 * Calculate the TF-IDF value for the parcular word
	 * Wrtie the value to csv file, named by each hubs name
	 * 
	 * @param word allDocuments keySet 
	 * @return 
	 */
	private static ArrayList<ArrayList<String>> calculateTFIDF(String word, ArrayList<ArrayList<String>> allFDoc, ArrayList<String> keySet) {
		ArrayList<ArrayList<String>> tfidfValue=new ArrayList<ArrayList<String>>();
		double tfidf;
		for(int i=0;i<allFDoc.size();i++){
			ArrayList<String> output=new ArrayList<String>();
			ArrayList<String> document=allFDoc.get(i);
			String key=keySet.get(i);			
			double tf=calculateTF(word,document);
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
		double appear=0.0;
		for(ArrayList<String> d: allFDoc){
			if(d.contains(word)){
				appear++;
			}
		}
		if(appear!=0.0){
			idf=Math.log((double)(n/appear));	
		}
		else{
			idf=0.0;
		}
		return idf;
	}

	/**
	 * Calculate the TF value for the word
	 * @return 
	 */
	private static double calculateTF(String word,
			ArrayList<String> document) {
		double tf=0;
		double frequency=0.0;
		if(document.contains(word)){
			frequency++;
		}
		tf=frequency/document.size();
		return tf;
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
		 for(ArrayList<String> user: users){
			 System.out.println(user.toString()); 
		 }
		 
	 }	 

	/**
	 * Get the ID of the hubs
	 */
	public static ArrayList<String> getHubs() throws IOException {
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
	
	/**
	 * Get the ID of the outliers
	 */
	public static ArrayList<String> getOutliers() throws IOException {
			AzureIO.downloadFromAzure(cloudFilePath,containerName, outputFilePath);
			ArrayList<ArrayList<String>> contents=CsvFileIO.readCSVFile(outputFilePath);
			ArrayList<String> outliers= new ArrayList<String>();
			for(ArrayList<String> c: contents){
				if(c.get(0).equals("Group:outliers")){
					for(int i=1;i<c.size();i++){
						outliers.add(c.get(i).trim());
					}
					
				}
			}
			return outliers;
	 }







	
}
