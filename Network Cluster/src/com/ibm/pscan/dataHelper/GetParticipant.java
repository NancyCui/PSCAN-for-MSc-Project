package com.ibm.pscan.dataHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.hadoop.conf.Configuration;

import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.io.TxtFileIO;

/**
 * Get the participation information in the inputfile
 * Write to the specific outputfile
 * 
 * @author Ningxin
 */

public class GetParticipant {
	
	private static int column = 6; //get the participations data
			
	/**
	 * Get the specific data (key, value) pair- the participants information
	 * 
	 * @param message
	 */
	private  static ArrayList<String> getParticipants(ArrayList<ArrayList<String>> message, int column) {
		ArrayList<String> participants=new ArrayList<String>();
		String textContent;
		for(int i=0; i<message.size();i++){
			textContent=message.get(i).get(column);			
			if(textContent!=""){
				//delete "users:"
				textContent=textContent.replaceAll("user:","");
				if(!textContent.contains(",")){
					textContent=textContent+","+textContent;					
				}
				participants.add(textContent);	
			}							
		}
		return participants;
	}
	

	/**
	 * When the value in column "participant" is null
	 * Get the replied_to_id, threadID and the senderID
	 * If the replied_to_id is null, set as thread_id
	 */
	private static ArrayList<String> getReply(ArrayList<ArrayList<String>> message) {
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
	public static void participant(String input_Path, String output_Path, Configuration conf) throws IOException{		
		ArrayList<ArrayList<String>> message=CsvFileIO.readFile(input_Path);
		ArrayList<String> participants=getParticipants(message, column);
		ArrayList<String> replyMessage=getReply(message);
		Collections.sort(replyMessage);
		TxtFileIO.insertFile(output_Path+"/"+"userRelation.txt", participants);
		TxtFileIO.insertFile(output_Path+"/"+"findNeighbor/reply.txt", replyMessage);
		System.out.println("Finish Writing");		
	}

}
