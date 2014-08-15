package com.ibm.pscan.dataHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
	
	private int column = 6; //get the participations data
	
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
	 * When the value in column "participant" is null
	 * Get the replied_to_id, threadID and the senderID
	 * If the replied_to_id is null, 
	 */
	private ArrayList<String> getReply(ArrayList<ArrayList<String>> message) {
		ArrayList<String> reply=new ArrayList<String>();	
		for(int i=0; i<message.size();i++){
			if(message.get(i).get(column).equals("")){				
				String textContent;
				
				String replied_to_id=message.get(i).get(1).replaceAll("	", "");
				String thread_id=message.get(i).get(2).replaceAll("	", "");
				String sender_id=message.get(i).get(9).replaceAll("	", "");
				
				if(replied_to_id.equals("")){
					textContent=thread_id+","+thread_id+"	"+sender_id;
				}
				else{
					if(replied_to_id.compareTo(sender_id)<=0){
						textContent=replied_to_id+","+thread_id+"	"+sender_id;
					}
					else{
						textContent=thread_id+","+replied_to_id+"	"+sender_id;
					}
				}				
				reply.add(textContent);
			}
			
		}
		return reply;
	}
	
	/**
	 * Read the inputfile and write the participant result into txt file
	 */
	private void participant() throws IOException{		
		ArrayList<ArrayList<String>> message=CsvFileIO.readFile(IOPath.GETPAR_INPUT);
		ArrayList<String> participants=getParticipants(message, column);
		ArrayList<String> replyMessage=getReply(message);
		Collections.sort(replyMessage);
		TxtFileIO.insertFile(IOPath.GETPAR_OUTPUT, participants);
		TxtFileIO.insertFile(IOPath.GETPAR_OUTPUT_REPLY, replyMessage);
		System.out.println("Finish Writing");		
	}


	public void relationship() throws IOException {
		participant();		
	}
	
}
