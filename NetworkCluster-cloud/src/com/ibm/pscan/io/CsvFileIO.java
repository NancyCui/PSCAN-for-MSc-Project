package com.ibm.pscan.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

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
	 * Read the file and return to ArrayList<ArrayList<String>>
	 */
	public static ArrayList<ArrayList<String>> readCSVFile(String filename) throws IOException{
		
		ArrayList<ArrayList<String>> contents=new ArrayList<ArrayList<String>>();						

		File file=new File(filename);		
			
		if(file.exists()){
			try{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String thisLine="";
				int i=0;
				while ((thisLine = reader.readLine()) != null) {
					String[] line=thisLine.split(",",0);
					contents.add(i,new ArrayList<String>());
					for(int j=0;j<line.length;j++){
						contents.get(i).add(line[j]);
					}
					i++;
				}
				reader.close();
			}			
			catch(Exception e){
				e.printStackTrace();
			}		
		}		
		return contents;		
	}
	
	/**
	 * Write the output to the CSV file on could
	 * @param conf 
	 * 
	 * @param filename content
	 */
	public static void writeListToCsv(String path, List<Double> contents, Configuration conf){
		try{
			Path pt=new Path(path);
	        FileSystem fs = FileSystem.get(conf);
	        BufferedWriter br=new BufferedWriter(new OutputStreamWriter(fs.create(pt,true)));
			String output = "";
			for(int i=0;i<contents.size();i++){
				output+=contents.get(i).toString();
				output+=(i==contents.size()-1?"":",");
			}					
			br.write(output);
            br.close();			
			
		}catch(Exception e){
			System.out.println("File not found");
		}
	}
	
	/**
	 * Write to CSV file on cloud
	 */
	public static void writeMapToCsv(String path,
			Map<String, ArrayList<String>> clusterMember, Configuration conf) throws IOException {
		try{
            Path pt=new Path(path);
            FileSystem fs = FileSystem.get(conf);
            BufferedWriter br=new BufferedWriter(new OutputStreamWriter(fs.create(pt,true)));
            Iterator<String> keys = clusterMember.keySet().iterator();
            String output="";
    		while(keys.hasNext()){
    			String key=keys.next().toString();
    			output+="Group:"+key.replace("[", "").replace("]", "")+",";
    			output+=clusterMember.get(key).toString().replace("[", "").replace("]", "");
    			output+=",";
    			output+=lineSep;
    		}	
            br.write(output);
            br.close();
		}catch(Exception e){
            System.out.println("File not found");
		}		
	}
	
	/**
	 * Write an array to CSV file on cloud
	 */
	public static void writeArrayToCSV(String path, String[] contents, Configuration conf){
		try{
			Path pt=new Path(path);
	        FileSystem fs = FileSystem.get(conf);
	        BufferedWriter br=new BufferedWriter(new OutputStreamWriter(fs.create(pt,true)));
			String output = "";
			for(int i=0;i<contents.length;i++){
				output+=contents[i].toString();
				output+=(i==contents.length-1?"":",");
			}					
			br.write(output);
            br.close();			
			
		}catch(Exception e){
			System.out.println("File not found");
		}
	}

	public static ArrayList<ArrayList<String>> readCsvFromCloud(String argsPath) {		
		ArrayList<ArrayList<String>> contents=new ArrayList<ArrayList<String>>();	
		
        try{
            Path pt=new Path(argsPath);
            FileSystem fs = FileSystem.get(new Configuration());
            BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
            String thisLine="";
			int i=0;
			while ((thisLine = br.readLine()) != null) {
				String[] line=thisLine.split(",",0);
				contents.add(i,new ArrayList<String>());
				for(int j=0;j<line.length;j++){
					contents.get(i).add(line[j]);
				}
				i++;
			}
        }catch(Exception e){
        	System.out.println("File not found");
        }
		return contents;
	}

	
	public static void insertCSVFile(String filename, ArrayList<String> contents){
		try{
			File file=new File(filename);
			if(!file.exists()){
				file.createNewFile();
			}
			String output = "";
			for(int i=0;i<contents.size();i++){
				output+=contents.get(i);
				output+=(i==contents.size()-1?"":",");
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
	
}
