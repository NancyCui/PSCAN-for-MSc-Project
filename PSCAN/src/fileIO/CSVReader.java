package fileIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.csvreader.CsvReader;

/**
 * Read the CSV file store it into an ArrayList<ArrayList<String>>
 * Write specific data into HBASE
 * @author Ningxin
 *
 */
public class CSVReader {
	
	private static String basePath = "/Users/Nancy/Documents/Java/PSCAN/";
	private static String inputFileName=basePath+"Messages.csv";
	private static String outputFileName=basePath + "input/userRelation2.txt";
	public static String lineSep=System.getProperty("line.separator");
	
	/**
	 * Read file from "inputFileName" and store the whole file into an ArrayList<ArrayList<String>>
	 * each line stores into an ArrayList<String>
	 * @param filename
	 * @return content
	 * @throws IOException
	 */
	private static ArrayList<ArrayList<String>> readFile(String filename) throws IOException{
		
		ArrayList<String> header=new ArrayList<String>();
		ArrayList<ArrayList<String>> content=new ArrayList<ArrayList<String>>();						
		
		boolean firstTime=true; //used for read the header
		CsvReader csvReader = new CsvReader(filename, ',',Charset.forName("GBK"));
		csvReader.readHeaders();
		while (csvReader.readRecord()) {
			//Read the header of the CSV file and store them into an ArrayList called header
			if(firstTime){
				int colNum=csvReader.getColumnCount();
				for(int i=0;i<colNum;i++){
					header.add(csvReader.getHeader(i));
				}
				firstTime=false;
			}
			//Read the body of the CSV file store into "content"
			ArrayList<String> body=new ArrayList<String>();
			for(int i=0;i<header.size();i++){								
				body.add(csvReader.get(header.get(i)));
			}
			content.add(body);
		}
		return content;
	}
	
	public static void insertFile(String filename, ArrayList<String> contents){
		try{
			File file=new File(filename);
			if(!file.exists()){
				file.createNewFile();
			}
			String output = "";
			for(int i=0;i<contents.size();i++){
				output+=contents.get(i);
				output+=(i==contents.size()-1?"":"\n");
			}
			output+=lineSep;		
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
			writer.write(output);
			writer.flush();
			writer.close();			
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
	
	/**
	 * Get the specific data (key, value) pair
	 * @param message
	 */
	private static ArrayList<String> getParticipants(ArrayList<ArrayList<String>> message, int column) {
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
		System.out.println(participants.toString());
		return participants;
	}
	
	public static void main(String[] args) throws IOException {
		ArrayList<ArrayList<String>> message=readFile(inputFileName);
		int column = 6; //get the participations data
		ArrayList<String> participants=getParticipants(message, column);		
		System.out.println("Writing...");
		insertFile(outputFileName, participants);
		System.out.println("Finish");
	}


}
