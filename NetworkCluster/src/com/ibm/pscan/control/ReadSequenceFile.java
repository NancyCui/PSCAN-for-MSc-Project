package com.ibm.pscan.control;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import com.ibm.pscan.io.SequenceFileIO;
import com.ibm.pscan.type.ArrayListWritable;

public class ReadSequenceFile {
	public static void main(String[] args) throws Exception {
		
		  String dst = "/Users/Nancy/Downloads";  
		  Configuration conf = new Configuration();  
		  FileSystem fs = FileSystem.get(URI.create(dst), conf);
		  FileStatus fileList[] = fs.listStatus(new Path(dst));
		  int size = fileList.length;
		  for(int i = 0; i < size; i++){
			  if( fileList[i].getPath().toString().contains("part")){ 
				  ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> vertexs=SequenceFileIO.readSequenceFileTAA(fs, fileList[i].getPath() , conf);
				    for(int j=0;j<vertexs.size();j++){
			    	System.out.println(vertexs.get(j).toString());
			    }
			  }
		  
		  }
		  fs.close();	
		
//		Configuration conf=new Configuration();
//		
//		String fileVertex="/Users/Nancy/Downloads/part-r-00000";
//
//
//	    FileSystem fsVertex = FileSystem.get(URI.create(fileVertex), conf);
//	    Path pathVertex = new Path(fileVertex);
//
//	   
//	    
//	    //read the sequence file
//	    ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> vertexs=SequenceFileIO.readSequenceFileTAA(fsVertex, pathVertex, conf);   
//	    
//	    for(int i=0;i<vertexs.size();i++){
//	    	System.out.println(vertexs.get(i).toString());
//	    }
	}
}
