package com.ibm.pscan.mapreduce;
import com.ibm.pscan.type.ArrayListWritable;
import com.ibm.pscan.util.IOPath;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
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


/**
 * Cluster the nodes, if it is a non-member node, tag it.
 * @author Ningxin
 */

public class LPCCMapReduce {
	
	private static int loopCount=0;
	private static boolean iterateLPCC=true; 
	
	private static LPCCMapReduce getLPCCMapReduceInstance=null;
	

	private LPCCMapReduce(){}
	
	public static LPCCMapReduce getInstance(){
		if(getLPCCMapReduceInstance==null){
			getLPCCMapReduceInstance=new LPCCMapReduce();
		}
		return getLPCCMapReduceInstance;
	}
	
	
	
	/**
	 * Input: <Text, ArrayListWritable>
	 * key is the input vertex ID
	 * value is the structure information of the input vertex
	 * Output: <Text, ArrayListWritable>
	 * key' is a vertex ID
	 * value' is the label or the structure information of the input vertex
	 */
	private static class doLPCCMapper extends Mapper<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>> {		
		//the {label} is stored into a {} -> {{label}}
		private ArrayListWritable<ArrayListWritable<Text>> labelValue=new ArrayListWritable<ArrayListWritable<Text>>();
		
        public void map(Text key, ArrayListWritable<ArrayListWritable<Text>> value, Context context) throws IOException, InterruptedException {        	       	
        	
        	//get the label from the structure information and store it into labelValue
        	labelValue.clear();
        	ArrayListWritable<Text> label=new ArrayListWritable<Text>(value.get(1));
    		labelValue.add(label);
    		
        	/*
        	 * Get the status of the vertex
        	 * If the vertex is activated, for each neighbor vi in the adjacency list
        	 * take vi as key' and take the label of the input vertex as value'
        	 */
        	String status=value.get(0).get(0).toString();
        	if(status.equals("activated")){       		
        		ArrayListWritable<Text> adjacencyList=new ArrayListWritable<Text>(value.get(2));      		
        		for(int adListSize=0;adListSize<adjacencyList.size();adListSize++){       			
        			context.write(adjacencyList.get(adListSize), labelValue);
        		}        		
        	}
        	
        	//Take the vertex ID as key' and the structure information of the vertex as value'
        	context.write(key, value); 
        }
    }
	
	
	/**
	 * Input: <Text, ArrayListWritable>
	 * key is vertex ID
	 * values include the structure information of the vertex and the labels from its neighbors
	 * Output: <Text, ArrayListWritable>
	 * key' is the vertex ID
	 * value' is the updated structure information of the vertex
	 */
	private static class doLPCCReducer extends Reducer<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>> {
		
		public void reduce(Text key, Iterable<ArrayListWritable<ArrayListWritable<Text>>> values, Context context) throws IOException, InterruptedException {   
			String newLabel="";
			String oldLabel="";
			ArrayListWritable<ArrayListWritable<Text>> strInfo=new ArrayListWritable<ArrayListWritable<Text>>();
			for(ArrayListWritable<ArrayListWritable<Text>> val:values){
				if(val.size()>1){
					strInfo=new ArrayListWritable<ArrayListWritable<Text>>(val);
				}
				else{
					String value=val.get(0).get(0).toString();
					if(newLabel.equals("")){
						newLabel=value;
					}
					else{
						if(newLabel.compareTo(value)>0){
							newLabel=value;
						}
					}					
				}
			}
			oldLabel=strInfo.get(1).get(0).toString();
			if(oldLabel.compareTo(newLabel)>0&&(!newLabel.equals(""))){
				strInfo.get(1).get(0).set(new Text(newLabel));
				iterateLPCC=true;
			}
			else{
				strInfo.get(0).get(0).set(new Text("inactivated"));
			}
			context.write(key,strInfo);						
		}

	}	
	
	/**
	 * Find Non-member nodes
	 * Read from the input files and get the key value pair for the reduce process 
	 */
	private static class findNonMemberMapper extends Mapper<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>> {
       
		public void map(Text key, ArrayListWritable<ArrayListWritable<Text>> value, Context context) throws IOException, InterruptedException {
        	Text newKey=new Text(key.toString().replaceAll("	", ""));
        	context.write(newKey, value);           
        }
    }
	
	private static class findNonMemberCombiner extends Reducer<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>>{
		
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
	private static class findNonMemberReducer extends Reducer<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>> {
        
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
	
	
	
/*------------------------Configuration of mapperClass and reducerClass-------------------------------------------------------------------------------------------------*/
	private boolean doLPCC(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		
		iterateLPCC=false;
		
		Job doLPCCJob = new Job(conf, "cluster");
		doLPCCJob.setJarByClass(LPCCMapReduce.class);
		doLPCCJob.setMapperClass(doLPCCMapper.class);	    
		doLPCCJob.setReducerClass(doLPCCReducer .class);
		doLPCCJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		doLPCCJob.setInputFormatClass(SequenceFileInputFormat.class);
		doLPCCJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		doLPCCJob.setMapOutputKeyClass(Text.class); 
		doLPCCJob.setMapOutputValueClass(ArrayListWritable.class); 
	    
		doLPCCJob.setOutputKeyClass(Text.class);
		doLPCCJob.setOutputValueClass(ArrayListWritable.class);
	    
	    FileInputFormat.addInputPath(doLPCCJob, new Path(input));
	    FileOutputFormat.setOutputPath(doLPCCJob, new Path(output));	    
		
	    return doLPCCJob.waitForCompletion(true);		
		
	}
	

	private boolean findNonMember(Configuration conf,
			String input1, String input2, String output) throws IOException, ClassNotFoundException, InterruptedException {
		
		Job findNonMemberJob = new Job(conf, "cluster-findNonMember");
		findNonMemberJob.setJarByClass(LPCCMapReduce.class);
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
	    
	    FileInputFormat.addInputPath(findNonMemberJob, new Path(input1));
	    FileInputFormat.addInputPath(findNonMemberJob, new Path(input2));
	    FileOutputFormat.setOutputPath(findNonMemberJob, new Path(output));	    
		
	    return findNonMemberJob.waitForCompletion(true);
	}
	
/*------------------------Main Method-------------------------------------------------------------------------------------------------*/		
	public void LPCC(Configuration conf, String inputFile, String outputFile) throws Exception {
		
		boolean successLPCC=true;
		boolean successNonMember;
		FileSystem fs = FileSystem.get(conf);
		
		/*
		 * iterate the Map and the Reduce process until all status is in-activated
		 */
		while(successLPCC){
			while(iterateLPCC){
				if(loopCount==0){
					successLPCC=doLPCC(conf,inputFile, outputFile);
					loopCount++;
				}
				else if((loopCount%2)==0&&loopCount!=0){
					fs.delete(new Path(outputFile), true);
					successLPCC=doLPCC(conf,IOPath.LPCC_OUTPUT2, outputFile);
					loopCount++;
				}
				else{
					if(fs.exists(new Path(IOPath.LPCC_OUTPUT2))){
						fs.delete(new Path(IOPath.LPCC_OUTPUT2),true);
					}
					successLPCC=doLPCC(conf,outputFile, IOPath.LPCC_OUTPUT2);
					loopCount++;
				}
			}
			
			/*
			 * format the name of the output file
			 */
			if(loopCount%2==0&&loopCount!=0){
				fs.delete(new Path(outputFile),true);
				fs.rename(new Path(IOPath.LPCC_OUTPUT2), new Path(outputFile));
			}
			else{
				if(fs.exists(new Path(IOPath.LPCC_OUTPUT2))){
					fs.delete(new Path(IOPath.LPCC_OUTPUT2),true);
				}
			}
			
			/*
			 * Find hubs and outliers in network
			 */
			
			successNonMember=findNonMember(conf,outputFile, IOPath.LPCC_ADLIST_INPUT, IOPath.LPCC_OUTPUT2);
			
			if(successNonMember){
				fs.delete(new Path(outputFile),true);
				fs.rename(new Path(IOPath.LPCC_OUTPUT2), new Path(outputFile));				
			}
			
			break;
		
		}		
		
	}

}
