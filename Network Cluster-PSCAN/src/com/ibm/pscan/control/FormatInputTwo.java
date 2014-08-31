package com.ibm.pscan.control;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.ibm.pscan.mapreduce.PSCANMapReduce;
import com.ibm.pscan.mapreduce.ParticipantMapReduce;
import com.ibm.pscan.util.Config;

/**
 * Get the participant information from the input file
 * 
 * @author Ningxin
 */

public class FormatInputTwo {
	
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		String[] otherArgs = new String[1];
		otherArgs[0]=Config.FORMAT_INPUT_TWO;

	    //if the outputFile exists, delete the outputfile
	    if(fs.exists(new Path(otherArgs[0]+"/"+"deleteDuplication"))){
	    	fs.delete(new Path(otherArgs[0]+"/"+"deleteDuplication"), true);
	    }
	    if(fs.exists(new Path(otherArgs[0]+"/"+"participantOutput"))){
	    	fs.delete(new Path(otherArgs[0]+"/"+"participantOutput"), true);
	    }
	    
	    
		PSCANMapReduce.deleteDuplication(conf, otherArgs[0]);
		
		ParticipantMapReduce.findConnections(conf, otherArgs[0]);

		
	}

}
