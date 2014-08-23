package com.ibm.pscan.dataHelper;

import java.io.IOException;
import java.util.ArrayList;

import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.util.IOPath;

public class GetMessage {
	
	private static int columnID = 9; //get each message's sender ID
	private static int columnContent=13; //get the message content of the sender
	
	public static String lineSep=System.getProperty("line.separator");
	
	public static ArrayList<ArrayList<String>> getMessages(ArrayList<ArrayList<String>> message) {
		ArrayList<ArrayList<String>> messages=new ArrayList<ArrayList<String>>();		
		String textID;
		String textContent;
		for(int i=0; i<message.size();i++){
			ArrayList<String> m=new ArrayList<String>();
			textID=message.get(i).get(columnID);
			textContent=message.get(i).get(columnContent).replace(lineSep, "").replace("	", "");
			m.add(textID);	
			m.add(textContent);
			messages.add(m);
		}
		return messages;
	}
	
	/**
	 * Read the inputfile and write the participant result into txt file
	 * @return 
	 */
	public static ArrayList<ArrayList<String>> messages() throws IOException{		
		ArrayList<ArrayList<String>> message=CsvFileIO.readFile(IOPath.GETPAR_INPUT);
		ArrayList<ArrayList<String>> messages=getMessages(message);
		return messages;	
	}
	
	
}
