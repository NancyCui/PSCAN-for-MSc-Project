package com.ibm.pscan.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.csvreader.CsvReader;

/**
 * Read the CSV file store it into an ArrayList<ArrayList<String>>
 * Write specific data into HBASE
 * 
 * @author Ningxin
 *
 */
public class CsvFileIO {
	
	public static String lineSep=System.getProperty("line.separator");
	
	/**
	 * Read file from "inputFileName" and store the whole file into an ArrayList<ArrayList<String>>
	 * each line stores into an ArrayList<String>
	 * 
	 * @param filename
	 * @return content
	 * @throws IOException
	 */
	public static ArrayList<ArrayList<String>> readFile(String filename) throws IOException{
		
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
	
}
