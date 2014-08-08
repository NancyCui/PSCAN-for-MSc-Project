package fileIO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class TxtFileIO {
	
	public static String lineSep=System.getProperty("line.separator");
	
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
	

}
