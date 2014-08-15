package com.ibm.pscan.control;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.ibm.pscan.dataHelper.GetParticipant;
import com.ibm.pscan.mapreduce.AdListMapReduce;
import com.ibm.pscan.mapreduce.ParticipantMapReduce;
import com.ibm.pscan.util.IOPath;

/**
 * Get the participant information from the input file
 * @author Nancy
 *
 */
public class FormatInput {
	
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		File theDir = new File(IOPath.GETPAR_OUTPUT_REPLY_BASE);
		theDir.mkdirs();
		
		GetParticipant getParti=GetParticipant.getInstance();
		getParti.relationship();
		AdListMapReduce adListMapReduce=AdListMapReduce.getInstance();
		adListMapReduce.deleteDup(conf, IOPath.GETPAR_OUTPUT_REPLY, IOPath.GETPAR_OUTPUT_REPLY_NODUP);
		ParticipantMapReduce participantMapReduce=ParticipantMapReduce.getInstance();
		participantMapReduce.getSenderReplyer(conf, IOPath.GETPAR_OUTPUT_REPLY_NODUP, IOPath.GETPAR_OUTPUT_ID);
		
		fs.delete(new Path(IOPath.GETPAR_OUTPUT_REPLY_BASE),true);
		fs.delete(new Path(IOPath.GETPAR_OUTPUT_BASE),true);
		
		System.out.println("finish...");
		
	}
}
