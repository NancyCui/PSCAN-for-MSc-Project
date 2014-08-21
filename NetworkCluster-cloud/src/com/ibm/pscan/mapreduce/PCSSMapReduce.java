package com.ibm.pscan.mapreduce;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.KeyFieldBasedPartitioner;

import com.ibm.pscan.control.PSCAN;
import com.ibm.pscan.type.ArrayListWritable;
import com.ibm.pscan.type.Edge;


/**
 * Calculate the structural similarity of edges
 * The output is the edge and the structural similarity of the edge
 * 
 * @author Ningxin
 */

public class PCSSMapReduce {

	public static double thresHold=PSCAN.thresHold; 

	
	/**
	 * Key is the input vertex, value is the adjacency list of the input vertex
	 * Key' is the edge connecting the input vertex and its neighbor
	 * Value' is the adjacency list of the input vertex
	 * @author Ningxin
	 *
	 */
	public static class doPCSSMapper extends Mapper<Text, ArrayListWritable<ArrayListWritable<Text>>, Edge, ArrayListWritable<Text>> {
		
        public void map(Text key, ArrayListWritable<ArrayListWritable<Text>> value, Context context) throws IOException, InterruptedException {
        	
        	ArrayList<ArrayList<String>> keyArray=new ArrayList<ArrayList<String>>();       
        	for(int i=0;i<value.get(2).size();i++){
        		if(i!=0){
	        		ArrayList<String> edge=new ArrayList<String>();
	        		edge.add(key.toString().replaceAll("	", ""));
	        		edge.add(value.get(2).get(i).toString());	        		
	        		keyArray.add(edge);
        		}
        	}
        	
        	for(int i=0;i<keyArray.size();i++){
        		String inputVertex=keyArray.get(i).get(0).toString();
        		String neighbor=keyArray.get(i).get(1).toString();
        		Edge edge=new Edge(inputVertex,neighbor);
        		context.write(edge, value.get(2));
        	}
        	
        	          
        }
    }
	
	/**
	 * Key' is the edge connecting the input vertex and its neighbor
	 * Value' is the structural similarity of the edge 
	 * Only output the edges whose structural similarity > = threshold
	 * @author Ningxin
	 *
	 */
	public static class doPCSSReducer extends Reducer<Edge, ArrayListWritable<Text>, Edge, DoubleWritable> {;
		
		private DoubleWritable strSimVal=new DoubleWritable();
		
        public void reduce(Edge key, Iterable<ArrayListWritable<Text>> values, Context context) throws IOException, InterruptedException {                      
        	ArrayListWritable<Text> inputList =new ArrayListWritable<Text>();
        	ArrayListWritable<Text> neighborList=new ArrayListWritable<Text>();
        	double structuralSimilarity;
        	
        	int i=0;
        	for(ArrayListWritable<Text> val:values){
        		if(i==0){
        			inputList=new ArrayListWritable<Text>(val);
        			i++;
        		}
        		else{
        			neighborList=new ArrayListWritable<Text>(val);
        		}
        	}
        	
        	double common=0;
        	for(int inputIndex=0; inputIndex<inputList.size();inputIndex++){
        		for(int neighborIndex=0; neighborIndex<neighborList.size();neighborIndex++){
        			if(inputList.get(inputIndex).toString().compareTo(neighborList.get(neighborIndex).toString())==0){
        				common++;
        				break;
        			}
        		}
        	}
        	structuralSimilarity=(common)/Math.sqrt(inputList.size()*neighborList.size());
        	strSimVal.set(structuralSimilarity);
        	if(structuralSimilarity>=thresHold){
        		context.write(key, strSimVal);
        	}
        	 
        }
	}
	
	public static boolean doPCSS(Configuration conf, String path) throws IOException, ClassNotFoundException, InterruptedException {
		
		Job doPCSSJob = new Job(conf, "calculate the structural similarity");

		doPCSSJob.setJarByClass(PCSSMapReduce.class);
		doPCSSJob.setMapperClass(doPCSSMapper.class);	    
		doPCSSJob.setReducerClass(doPCSSReducer .class);
		doPCSSJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		doPCSSJob.setInputFormatClass(SequenceFileInputFormat.class);
		doPCSSJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		doPCSSJob.setMapOutputKeyClass(Edge.class); 
		doPCSSJob.setMapOutputValueClass(ArrayListWritable.class); 
	    
		doPCSSJob.setOutputKeyClass(Edge.class);
		doPCSSJob.setOutputValueClass(DoubleWritable.class);
	    
	    FileInputFormat.addInputPath(doPCSSJob, new Path(path+"/"+"adjacencyList"));
	    FileOutputFormat.setOutputPath(doPCSSJob, new Path(path+"/"+"pcss"));
	    
	    return doPCSSJob.waitForCompletion(true);		
		
	}

}
