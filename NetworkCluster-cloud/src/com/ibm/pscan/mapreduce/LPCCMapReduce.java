package com.ibm.pscan.mapreduce;
import com.ibm.pscan.io.SequenceFileIO;
import com.ibm.pscan.type.ArrayListWritable;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
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
 * Cluster the nodes
 * 
 * @author Ningxin
 */

public class LPCCMapReduce {
	
	private static int loopCount=0;
		
	/**
	 * Input: <Text, ArrayListWritable>
	 * key is the input vertex ID
	 * value is the structure information of the input vertex
	 * Output: <Text, ArrayListWritable>
	 * key' is a vertex ID
	 * value' is the label or the structure information of the input vertex
	 */
	public static class doLPCCMapper extends Mapper<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>> {		
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
	public static class doLPCCReducer extends Reducer<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, ArrayListWritable<ArrayListWritable<Text>>> {
		
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
			}
			else{
				strInfo.get(0).get(0).set(new Text("inactivated"));
			}
			context.write(key,strInfo);						
		}

	}	
	
	public static boolean doLPCC(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		
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
	

	public static void LPCC(Configuration conf, String path) throws Exception {
		
		boolean loop=true;
		FileSystem fs = FileSystem.get(conf);
		
		String inputFile=path+"/"+"lpccInput";
		String outputFile=path+"/"+"lpccOutput";
		String outputFile2=path+"/"+"lpccOutput2";
		
		while(loop){
			if(loopCount==0){
				doLPCC(conf,inputFile, outputFile);
				loopCount++;
			}
			else{										
				if((loopCount%2)==0&&loopCount!=0){
					loop=checkStatus(outputFile,fs,conf);
					if(loop==true){
						fs.delete(new Path(outputFile), true);
						doLPCC(conf,outputFile2, outputFile);
						loopCount++;
					}
				}
				else{
					if(fs.exists(new Path(outputFile2))){
						loop=checkStatus(outputFile2,fs,conf);
						if(loop==true){
							fs.delete(new Path(outputFile2),true);
						}							
					}
				doLPCC(conf,outputFile, outputFile2);
				loopCount++;
				}
			}
		}
		
		/*
		 * format the name of the output file
		 */
		if(loopCount%2==0&&loopCount!=0){
			fs.delete(new Path(outputFile),true);
			fs.rename(new Path(outputFile2), new Path(outputFile));
		}
		else{
			if(fs.exists(new Path(outputFile2))){
				fs.delete(new Path(outputFile2),true);
			}
		}	
		
	}


	private static boolean checkStatus(String file, FileSystem fs, Configuration conf) throws IOException {


	    FileSystem fsVertex = FileSystem.get(URI.create(file), conf);
	    FileStatus fileList[] = fsVertex.listStatus(new Path(file));
	    
	    int size = fileList.length;
		  for(int i = 0; i < size; i++){
			  if( fileList[i].getPath().toString().contains("part")){
				  ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> vertexs=SequenceFileIO.readSequenceFileTAA(fsVertex, fileList[i].getPath() , conf);   
				    for(int j=0;j<vertexs.size();j++){
				    	if(vertexs.get(j).get(1).get(0).toString().equals("activated")){
				    		return true;
				    	}
				    }
			  }
		  
		  }
		 fs.close();	

		return false;
	}

}
