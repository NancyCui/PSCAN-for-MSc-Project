package com.ibm.pscan.dataHelper;

import java.io.IOException;
import java.util.ArrayList;

import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.util.Config;
import com.ibm.pscan.util.IOPath;

/**
 * Get the user details from users.csv
 * 
 * @author Ningxin
 */

public class GetDetails {
	
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
	
	private static ArrayList<ArrayList<String>> getUsers(ArrayList<ArrayList<String>> user) {
		ArrayList<ArrayList<String>> users=new ArrayList<ArrayList<String>>();
		for(int i=0; i<user.size();i++){
			ArrayList<String> u=new ArrayList<String>();
			for(int j=0;j<=5;j++){
				u.add(user.get(i).get(j));
			}
			users.add(u);
		}
		return users;
	}
	
	/**
	 * Read the inputfile and return the participant result
	 */
	public static ArrayList<ArrayList<String>> messages() throws IOException{		
		ArrayList<ArrayList<String>> message=CsvFileIO.readFile(IOPath.GETPAR_INPUT);
		ArrayList<ArrayList<String>> messages=getMessages(message);
		return messages;	
	}
	
	
	/**
	 * Read the inputfile and return the user details
	 */
	public static ArrayList<ArrayList<String>> userDetails() throws IOException{		
		ArrayList<ArrayList<String>> user=CsvFileIO.readFile(Config.STORAGE_FILE_NAME_USER);		
		ArrayList<ArrayList<String>> users=getUsers(user);
		return users;	
	}

}
