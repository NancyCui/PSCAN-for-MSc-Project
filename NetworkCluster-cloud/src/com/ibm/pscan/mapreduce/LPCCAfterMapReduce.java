package com.ibm.pscan.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.KeyFieldBasedPartitioner;

import com.ibm.pscan.type.ArrayListWritable;

/**
 * Find hubs and outliers in network
 * 
 * @author Ningxin
 */
public class LPCCAfterMapReduce {
	
	/**
	 * Find Non-member nodes
	 * Read from the input files and get the key value pair for the reduce process 
	 */
	public static class findNonMemberMapper extends Mapper<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>> {
       
		public void map(Text key, ArrayListWritable<ArrayListWritable<Text>> value, Context context) throws IOException, InterruptedException {
        	Text newKey=new Text(key.toString().replaceAll("	", ""));
        	context.write(newKey, value);           
        }
    }
	
	public static class findNonMemberCombiner extends Reducer<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>>{
		
		public void reduce(Text key, Iterable<ArrayListWritable<ArrayListWritable<Text>>> values, Context context) throws IOException, InterruptedException { 
			 for(ArrayListWritable<ArrayListWritable<Text>> val: values){
				 context.write(key, val);
			 }
		 }
	}
	
	/**
	 * Reformate the key value pair to get the final result
	 * Input:
	 * Key is the vertexID
	 * Value is the structural information
	 * The label in the structural is: inactivated/adList
	 * Output:
	 * Key' is the vertexID
	 * Value' is the re-formated structural information
	 * [[inactivated/adList], [clusterID], [adjacency list]]
	 * The adjacency list is the one before cutting stage
	 */
	public static class findNonMemberReducer extends Reducer<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>> {
        
		public void reduce(Text key, Iterable<ArrayListWritable<ArrayListWritable<Text>>> values, Context context) throws IOException, InterruptedException {                      
        	ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> newValues=new ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>>();
        	ArrayListWritable<ArrayListWritable<Text>> value=new ArrayListWritable<ArrayListWritable<Text>>();
        	
        	for(ArrayListWritable<ArrayListWritable<Text>> val: values){
        		value=new ArrayListWritable<ArrayListWritable<Text>>(val);
        		newValues.add(value);       		
        	}
        	
        	/*
        	 * If there is only 1 value, means the nodes is neither a outlier or hub
        	 */
        	if(newValues.size()==1){        		
        		context.write(key, newValues.get(0));
        	}
        	else if(newValues.size()==2){
        		ArrayListWritable<ArrayListWritable<Text>> newStrInfo=new ArrayListWritable<ArrayListWritable<Text>>();
        		ArrayListWritable<Text> newStatus=new ArrayListWritable<Text>();
        		ArrayListWritable<Text> newLabel=new ArrayListWritable<Text>();
        		ArrayListWritable<Text> newAdList=new ArrayListWritable<Text>();
        		
        		for(ArrayListWritable<ArrayListWritable<Text>> val: newValues){
        			if(val.get(0).get(0).toString().equals("inactivated")){
        				newStatus.add(val.get(0).get(0));
        				newLabel.add(val.get(1).get(0));
        			}
        			else{
        				newAdList=new ArrayListWritable<Text>(val.get(2));
        			}
        			
        			
        		}
        		newStrInfo.add(newStatus);
    			newStrInfo.add(newLabel);
    			newStrInfo.add(newAdList);
        		context.write(key,newStrInfo);
        	}
        	else{
        		System.out.println("Error. The number of value is 0");
        	}
        }
	}
	
	
	
	public static boolean findNonMember(Configuration conf,
			String path) throws IOException, ClassNotFoundException, InterruptedException {
		
		Job findNonMemberJob = new Job(conf, "cluster-findNonMember");
		findNonMemberJob.setJarByClass(LPCCAfterMapReduce.class);
		findNonMemberJob.setMapperClass(findNonMemberMapper.class);	 
		findNonMemberJob.setCombinerClass(findNonMemberCombiner.class);
		findNonMemberJob.setReducerClass(findNonMemberReducer .class);
		findNonMemberJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		findNonMemberJob.setInputFormatClass(SequenceFileInputFormat.class);
		findNonMemberJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		findNonMemberJob.setMapOutputKeyClass(Text.class); 
		findNonMemberJob.setMapOutputValueClass(ArrayListWritable.class); 
	    
		findNonMemberJob.setOutputKeyClass(Text.class);
		findNonMemberJob.setOutputValueClass(ArrayListWritable.class);
	    
	    FileInputFormat.addInputPath(findNonMemberJob, new Path(path+"/"+"lpccOutput"));
	    FileInputFormat.addInputPath(findNonMemberJob, new Path(path+"/"+"adjacencyList"));
	    FileOutputFormat.setOutputPath(findNonMemberJob, new Path(path+"/"+"lpccAfterOutput"));	    
		
	    return findNonMemberJob.waitForCompletion(true);
	}
}
