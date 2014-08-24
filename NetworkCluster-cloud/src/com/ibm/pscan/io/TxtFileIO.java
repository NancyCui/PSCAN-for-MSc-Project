package com.ibm.pscan.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * The input and output of txt file
 * 
 * @param filename and list elements
 * 
 * @author Ningxin
 */

public class TxtFileIO {
	
	public static String lineSep=System.getProperty("line.separator");
	
	/**
	 * Insert the contents in a ArrayList<String> into a txt file
	 */
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
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,false));
			writer.write(output);
			writer.flush();
			writer.close();						
		}catch(Exception e){
			e.printStackTrace();
		}			
	}	
	
	
	/**
	 * Write an arrayList to txt file on cloud
	 */
	public static void writeArrayListToTxt(String path, ArrayList<String> contents){
		try{
			Configuration conf=new Configuration();
			Path pt=new Path(path);
	        FileSystem fs = FileSystem.get(conf);
	        BufferedReader bfr=new BufferedReader(new InputStreamReader(fs.open(pt))); 
	        String str = null;
	        String output = "";         
            while ((str = bfr.readLine())!= null)
            {
                output+=str; // write file content
                output+=lineSep;
             }
	        
			for(int i=0;i<contents.size();i++){
				output+=contents.get(i);
				output+=(i==contents.size()-1?"":"\n");
			}
			output+=lineSep;	
			BufferedWriter br=new BufferedWriter(new OutputStreamWriter(fs.create(pt,true))); 			
			br.write(output);
            br.close();			
			
		}catch(Exception e){
			System.out.println("File not found");
		}
	}
	

	
	

}
