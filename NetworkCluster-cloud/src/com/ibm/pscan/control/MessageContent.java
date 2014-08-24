package com.ibm.pscan.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.ibm.pscan.dataHelper.GetDetails;
import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.util.Config;
import com.ibm.pscan.util.IOPath;

public class MessageContent {
	
	private static String cloudFilePath="example/data/pscanOutput/result.csv";
	private static String outputFilePath=Config.BASE_PATH+"result.csv";
	private static String containerName=Config.CONTAINER_NAME;
	
	public static void main(String[] args) throws IOException {
		
		AzureIO.downloadFromAzure(cloudFilePath,containerName, outputFilePath);
		ArrayList<ArrayList<String>> contents=CsvFileIO.readCSVFile(outputFilePath);
		ArrayList<String> hubs= new ArrayList<String>();
		for(ArrayList<String> c: contents){
			if(c.get(0).equals("Group:hubs")){
				hubs.add(c.get(1));
			}
		}
		hubs.add("1521758242");
		ArrayList<ArrayList<String>> messages=GetDetails.messages();
		ArrayList<ArrayList<String>> users=GetDetails.userDetails();
		
		Map<String,ArrayList<String>> memberMessage=writeIntoMap(messages, hubs);
		
		System.out.println(memberMessage);
		
		//Put each member's message into a hashmap
		
		
		for(ArrayList<String> u: users){
			if(hubs.contains(u.get(0))){
				System.out.println(u.toString());
			}
			
		}
	}

	/**
	 * For each hubs, write the related word into a hash map
	 */
	private static Map<String, ArrayList<String>> writeIntoMap(ArrayList<ArrayList<String>> messages, ArrayList<String> hubs) {
		Map<String,ArrayList<String>> memberMessage= new HashMap<String,ArrayList<String>>();		
		String[] commons=IOPath.COMMON_WORDS;
		ArrayList<String> commonWords = new ArrayList<String>(Arrays.asList(commons));
		for(ArrayList<String> m: messages){
			if(hubs.contains(m.get(0))&&(!m.get(1).equals(""))){
				String clusterID=m.get(0);
				String message=m.get(1);
				StringTokenizer tokenizer = new StringTokenizer(message, " \t\n\r\f,.:;?![]/()'");
				if(memberMessage.containsKey(clusterID)){
					ArrayList<String> value=memberMessage.get(clusterID);
					while (tokenizer.hasMoreTokens()) {
						String w=tokenizer.nextToken().toLowerCase().replace("\"", "");
						if(!commonWords.contains(w)&&(!w.equals(""))){
							value.add(w);
						}						
				    }	
					Collections.sort(value);
					memberMessage.put(clusterID, value);
				}
				else{
					ArrayList<String> value=new ArrayList<String>();
					while (tokenizer.hasMoreTokens()) {
						String w=tokenizer.nextToken().toLowerCase();
						if(!commonWords.contains(w)){
							value.add(w);
						}						
				    }						
					memberMessage.put(clusterID, value);
				}
			}		
		}
		return memberMessage;		
	}
	
}
