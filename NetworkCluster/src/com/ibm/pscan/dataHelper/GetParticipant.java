package com.ibm.pscan.dataHelper;

import java.io.IOException;
import java.util.ArrayList;

import com.ibm.pscan.fileIO.CsvFileIO;
import com.ibm.pscan.fileIO.TxtFileIO;

/**
 * Get the participation information in the inputfile
 * Write to the specific outputfile
 * Call method "participant()"
 * 
 * @author Ningxin
 *
 */
public class GetParticipant {
	
	private static String basePath = "/Users/Nancy/Documents/Java/NetworkCluster/";
	private static String inputFileName=basePath+"ori/Messages.csv";
	private static String outputFileName=basePath + "input/userRelation2.txt";
	
	public GetParticipant(){
		
	}
	
	/**
	 * Get the specific data (key, value) pair- the participants information
	 * 
	 * @param message
	 */
	private ArrayList<String> getParticipants(ArrayList<ArrayList<String>> message, int column) {
		ArrayList<String> participants=new ArrayList<String>();
		String textContent;
		for(int i=0; i<message.size();i++){
			textContent=message.get(i).get(column);
			if(textContent!=""){
				//delete "users:"
				textContent=textContent.replaceAll("user:","");
				participants.add(textContent);	
			}							
		}
		return participants;
	}
	
	/**
	 * Read the inputfile and write the participant result into txt file
	 */
	private void participant() throws IOException{		
		ArrayList<ArrayList<String>> message=CsvFileIO.readFile(inputFileName);
		int column = 6; //get the participations data
		ArrayList<String> participants=getParticipants(message, column);		
		TxtFileIO.insertFile(outputFileName, participants);
		System.out.println("Finish Writing");		
	}

	
	public static void main(String[] args) throws IOException {
		GetParticipant getParti=new GetParticipant();
		getParti.participant();		
	}


	
}
