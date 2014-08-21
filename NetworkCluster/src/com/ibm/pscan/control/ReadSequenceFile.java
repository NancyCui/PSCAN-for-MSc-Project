package com.ibm.pscan.control;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import com.ibm.pscan.io.SequenceFileIO;
import com.ibm.pscan.type.ArrayListWritable;

public class ReadSequenceFile {
	public static void main(String[] args) throws Exception {
		Configuration conf=new Configuration();
		
		String fileVertex="/Users/Nancy/Downloads/part-r-00000";


	    FileSystem fsVertex = FileSystem.get(URI.create(fileVertex), conf);
	    Path pathVertex = new Path(fileVertex);

	   
	    
	    //read the sequence file
	    ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> vertexs=SequenceFileIO.readSequenceFileTAA(fsVertex, pathVertex, conf);   
	    
	    for(int i=0;i<vertexs.size();i++){
	    	System.out.println(vertexs.get(i).toString());
	    }
	}
}
