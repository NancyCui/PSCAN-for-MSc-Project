package com.ibm.pscan.mapreduce;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.KeyFieldBasedPartitioner;

import com.ibm.pscan.io.TxtFileIO;
import com.ibm.pscan.util.IOPath;


public class ParticipantMapReduce {

	private static ParticipantMapReduce getParticipantMapReduceInstance=null;
	
	private ParticipantMapReduce() {}	
	
	public static ParticipantMapReduce getInstance(){
		if(getParticipantMapReduceInstance==null){
			getParticipantMapReduceInstance=new ParticipantMapReduce();
		}
		return getParticipantMapReduceInstance;
	}
	
	/**
	 * Key: the <replied_to_id, threadID>.
	 * Value: senderID
	 */
	private static class findConnectionsMapper extends Mapper<Text, Text, Text, Text> {

        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {

        	context.write(key, value);           
        }
    }
	
	/**
	 * Combine the same key together
	 */
	private static class findConnectionsReducer extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {                      
	    	ArrayList<String> users=new ArrayList<String>();
	    	ArrayList<String> output=new ArrayList<String>();
	    	for (Text val : values){
	            users.add(val.toString().replaceAll("	", ""));
	        }
	    	if(users.size()==2){
	    		output.add(users.get(0)+","+users.get(1));
	    	}
	    	if(users.size()>2){
	    		for(int i=0;i<users.size();i++){
	    			for(int j=i+1;j<users.size();j++){
	    				output.add(users.get(i)+","+users.get(j));
	    			}
	    		}
	    		
	    	}
	    	if(output.size()>0){
	    		TxtFileIO.insertFile(IOPath.GETPAR_OUTPUT, output);  
	    	}
	    	 
        }
	}
	
	
	private boolean findConnections(Configuration conf, String input,
			String output) throws Exception {
		
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", "	");
		
		Job findConnectionsJob = new Job(conf, "find connections");
		findConnectionsJob.setJarByClass(AdListMapReduce.class);
		findConnectionsJob.setMapperClass(findConnectionsMapper.class);	    	    
		findConnectionsJob.setReducerClass(findConnectionsReducer.class);
		findConnectionsJob.setPartitionerClass(KeyFieldBasedPartitioner.class);

		findConnectionsJob.setMapOutputKeyClass(Text.class); 
		findConnectionsJob.setMapOutputValueClass(Text.class); 
	    
		findConnectionsJob.setOutputKeyClass(Text.class);	    
		findConnectionsJob.setOutputValueClass(Text.class);
		
		findConnectionsJob.setInputFormatClass(KeyValueTextInputFormat.class);
		findConnectionsJob.setOutputFormatClass(TextOutputFormat.class);
	    
	    FileInputFormat.addInputPath(findConnectionsJob, new Path(input));
	    FileOutputFormat.setOutputPath(findConnectionsJob, new Path(output));
	    
		return findConnectionsJob.waitForCompletion(true);
	}
	
	public void getSenderReplyer(Configuration conf, String inputFile, String outputFile) throws Exception {		
		findConnections(conf,inputFile,outputFile);
	}
}
