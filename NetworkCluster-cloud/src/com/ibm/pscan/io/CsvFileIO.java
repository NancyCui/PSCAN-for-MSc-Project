package com.ibm.pscan.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;

/**
 * Read the CSV file store it into an ArrayList<ArrayList<String>>
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
	
	/**
	 * Write the output to the CSV file
	 * 
	 * @param filename content
	 */
	public static void writeFile(String filename, List<Double> contents){
		try{
			File file=new File(filename);
			if(!file.exists()){
				file.createNewFile();
			}
			String output = "";
			for(int i=0;i<contents.size();i++){
				output+=contents.get(i).toString();
				output+=(i==contents.size()-1?"":",");
				output+=lineSep;
			}					
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
			writer.write(output);
			writer.flush();
			writer.close();			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void writeToCsv(String fileName,
			Map<String, ArrayList<String>> clusterMember) throws IOException {
		File file=new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		Iterator<String> keys = clusterMember.keySet().iterator();
		String output="";
		while(keys.hasNext()){
			String key=keys.next().toString();
			output+="Group:"+key.replace("[", "").replace("]", "")+",";
			output+=clusterMember.get(key).toString().replace("[", "").replace("]", "");
			output+=",";
			output+=lineSep;
		}	
		BufferedWriter writer = new BufferedWriter(new FileWriter(file,false));
		writer.write(output);
		writer.flush();
		writer.close();
	}

	
}
