package com.ibm.pscan.mapreduce;

import com.ibm.pscan.type.ArrayListWritable;
import com.ibm.pscan.util.IOPath;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.KeyFieldBasedPartitioner;

/**
 * Achieve the adjacency list of each vertex
 * key': v1
 * value': adjacency list of v1
 * 
 * @author Ningxin
 */

public class AdListMapReduce {

	
	/**
	 * Delete the duplicate lines in the input files.
	 */
	public static class deleteDuplicationMapper extends Mapper<Text, Text, Text, Text> {
    	private Text userRelationID=new Text();
 
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        	userRelationID.set(key);
        	context.write(userRelationID, new Text(""));           
        }
    }
	
	/**
	 * Delete the duplicate items
	 */
	public static class deleteDuplicationReducer extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {                      
        	context.write(key, new Text(""));
        }
	}
	
	/**
	 * For each key(fromID, toID)
	 * Write not only the key(fromID, toID) but also key(toID, fromID)
	 */
	public static class findNeighborReducer extends Reducer<Text, Text, Text, Text> {
		private Text invertKey=new Text(); //store the invert relationship, Example: (fromID, toID)->(toID, fromID)
		 
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {                      
        	context.write(key, new Text(""));
        	String[] invertK=key.toString().split(",");
        	String invertKString="";
        	for(int i=(invertK.length-1);i>=0;i--){
        		invertKString+=invertK[i];
        		if(i!=0){
        			invertKString+=",";
        		}
            invertKey.set(invertKString);
            }           
            context.write(invertKey, new Text(""));          
        }
    }
	
	/**
	 * Invert the key value pair and add to the original file
	 */
	public static class getAdjacencyListMapper extends Mapper<Text, Text, Text, Text> {
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {			
			context.write(value,key);
		}
	}
	
	/**
	 * (value,key)->same key together
	 * Ensure the type is consistent in MapperClass and ReducerClass
	 * The key' is the input vertex
	 * The value' is the adjacency list of the input vertex
	 */
	public static class getAdjacencyListReducer extends Reducer<Text, Text, Text, ArrayListWritable<ArrayListWritable<Text>>> {
		
	    public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException {	        
	    	String nodeLabel="adList";
	    	
	    	ArrayListWritable<ArrayListWritable<Text>> output=new ArrayListWritable<ArrayListWritable<Text>>();
	    	ArrayListWritable<Text> adjacencyList=new ArrayListWritable<Text>();
	    	ArrayListWritable<Text> status=new ArrayListWritable<Text>();
	    	ArrayListWritable<Text> label=new ArrayListWritable<Text>();
	    	
	    	String ownID=key.toString().replaceAll("	", "");
	    	adjacencyList.add(new Text(ownID));    
    		for(Text val:values){
    			String value=val.toString();
    			adjacencyList.add(new Text(value));
    		}
    		
    		status.add(new Text(nodeLabel));
    		label.add(new Text(ownID));
    		
    		output.add(status);
    		output.add(label);
    		output.add(adjacencyList);
    		
	    	context.write(key, output);
	    }
	}	
	

/*------------------------Configuration of mapperClass and reducerClass-------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Set the MapperClass and ReducerClass for the deleteDuplicationJob
	 * Job is used to delete the duplicate items in the input files
	 * 
	 * @param conf input output
	 * @return deleteDuplicationJob.waitForCompletion(true)
	 * @throws Exception
	 */
	public static boolean deleteDuplication(Configuration conf, String input,
			String output) throws Exception {
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", "\n");
		
		Job deleteDuplicationJob = new Job(conf, "delete the duplication relationship");
		deleteDuplicationJob.setJarByClass(AdListMapReduce.class);
		deleteDuplicationJob.setMapperClass(deleteDuplicationMapper.class);	    	    
		deleteDuplicationJob.setReducerClass(deleteDuplicationReducer.class);
		deleteDuplicationJob.setPartitionerClass(KeyFieldBasedPartitioner.class);

		deleteDuplicationJob.setMapOutputKeyClass(Text.class); 
		deleteDuplicationJob.setMapOutputValueClass(Text.class); 
	    
		deleteDuplicationJob.setOutputKeyClass(Text.class);	    
		deleteDuplicationJob.setOutputValueClass(Text.class);
		
		deleteDuplicationJob.setInputFormatClass(KeyValueTextInputFormat.class);
		deleteDuplicationJob.setOutputFormatClass(TextOutputFormat.class);
	    
	    FileInputFormat.addInputPath(deleteDuplicationJob, new Path(input));
	    FileOutputFormat.setOutputPath(deleteDuplicationJob, new Path(output));
	    
		return deleteDuplicationJob.waitForCompletion(true);
	}
	
	/**
	 * Find the vertex's neighbors
	 * 
	 * @param conf input output
	 * @throws Exception
	 */
	public static boolean findNeighbor(Configuration conf, String input,
			String output) throws Exception {
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", "\n");
		
		Job findNeighborJob = new Job(conf, "find neighbour of each verticle");
		findNeighborJob.setJarByClass(AdListMapReduce.class);
		findNeighborJob.setMapperClass(deleteDuplicationMapper.class);	    	    
		findNeighborJob.setReducerClass(findNeighborReducer.class);
		findNeighborJob.setPartitionerClass(KeyFieldBasedPartitioner.class);

		findNeighborJob.setMapOutputKeyClass(Text.class); 
		findNeighborJob.setMapOutputValueClass(Text.class); 
	    
		findNeighborJob.setOutputKeyClass(Text.class);	    
		findNeighborJob.setOutputValueClass(Text.class);
		
		findNeighborJob.setInputFormatClass(KeyValueTextInputFormat.class);
		findNeighborJob.setOutputFormatClass(TextOutputFormat.class);
	    
	    FileInputFormat.addInputPath(findNeighborJob, new Path(input));
	    FileOutputFormat.setOutputPath(findNeighborJob, new Path(output));
	    
		return findNeighborJob.waitForCompletion(true);
	}
	
	/**
	 * Get the adjacency list of each vertex
	 * 
	 * @param conf input output
	 * @throws IOException ClassNotFoundException InterruptedException
	 */
	public static boolean getAdjacencyList(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", ",");
		Job getAdjacencyListJob = new Job(conf, "get adjancency list");
		getAdjacencyListJob.setJarByClass(AdListMapReduce.class);
		getAdjacencyListJob.setMapperClass(getAdjacencyListMapper.class);	    
		getAdjacencyListJob.setReducerClass(getAdjacencyListReducer .class);
		getAdjacencyListJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		getAdjacencyListJob.setInputFormatClass(KeyValueTextInputFormat.class);
		getAdjacencyListJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		getAdjacencyListJob.setMapOutputKeyClass(Text.class); 
		getAdjacencyListJob.setMapOutputValueClass(Text.class); 
	    
		getAdjacencyListJob.setOutputKeyClass(Text.class);
		getAdjacencyListJob.setOutputValueClass(ArrayListWritable.class);
	    
	    FileInputFormat.addInputPath(getAdjacencyListJob, new Path(input));
	    FileOutputFormat.setOutputPath(getAdjacencyListJob, new Path(output));
	    
	    return getAdjacencyListJob.waitForCompletion(true);		
		
	}

	public static void adList(Configuration conf, String inputFile, String outputFile) throws Exception {
		
		boolean success=false;
		//delete the duplicate records in the original file and get the neighbor relation
		boolean successfulFindNeighbor=findNeighbor(conf,inputFile, IOPath.ADLIST_NEIGHBOR_OUTPUT);
		if(successfulFindNeighbor){
			boolean successDeleteDuplication=deleteDuplication(conf,IOPath.ADLIST_NEIGHBOR_OUTPUT,IOPath.ADLIST_RELATION_OUTPUT);
			//get the adjustList of each node
			if(successDeleteDuplication){
				success=getAdjacencyList(conf,IOPath.ADLIST_RELATION_OUTPUT, outputFile);
			}
		}
	
		if(success){
			FileSystem fs = FileSystem.get(conf);
			fs.delete(new Path(IOPath.ADLIST_NEIGHBOR_OUTPUT),true);
			fs.delete(new Path(IOPath.ADLIST_RELATION_OUTPUT),true);
		}
	}
	
	public static void deleteDup(Configuration conf, String inputFile, String outputFile) throws Exception {		
		deleteDuplication(conf,inputFile,outputFile);
	}

}
