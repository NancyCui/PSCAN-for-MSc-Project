package com.ibm.pscan.control;

import java.io.IOException;
import java.util.ArrayList;

import com.ibm.pscan.dataHelper.GetMessage;
import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.util.Config;

public class MessageContent {
	
	private static String cloudFilePath="example/data/pscanOutput/result.csv";
	private static String outputFilePath="/Users/Nancy/Desktop/result.csv";
	private static String containerName=Config.CONTAINER_NAME;
	
	public static void main(String[] args) throws IOException {
		
		AzureIO.downloadFromAzure(cloudFilePath,containerName, outputFilePath);
		ArrayList<ArrayList<String>> contents=CsvFileIO.readCSVFile(outputFilePath);
		for(ArrayList<String> c: contents){
			System.out.println(c.toString());
		}
		ArrayList<ArrayList<String>> messages=GetMessage.messages();
		for(ArrayList<String> m: messages){
			System.out.println(m.toString());
		}
	}
	
}
