package com.ibm.pscan.dataHelper;

import java.io.IOException;
import java.util.ArrayList;

import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.io.TxtFileIO;
import com.ibm.pscan.util.IOPath;

/**
 * Get the participation information in the inputfile
 * Write to the specific outputfile
 * Call method "participant()"
 * 
 * @author Ningxin
 *
 */
public class GetParticipant {
	
	private static GetParticipant getParticipantInstance=null;
	
	private GetParticipant() {
		
	}	
	
	public static GetParticipant getInstance(){
		if(getParticipantInstance==null){
			getParticipantInstance=new GetParticipant();
		}
		return getParticipantInstance;
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
		ArrayList<ArrayList<String>> message=CsvFileIO.readFile(IOPath.GETPAR_INPUT);
		int column = 6; //get the participations data
		ArrayList<String> participants=getParticipants(message, column);		
		TxtFileIO.insertFile(IOPath.GETPAR_OUTPUT, participants);
		System.out.println("Finish Writing");		
	}

	
	public void relationship() throws IOException {
		participant();		
	}


	
}
