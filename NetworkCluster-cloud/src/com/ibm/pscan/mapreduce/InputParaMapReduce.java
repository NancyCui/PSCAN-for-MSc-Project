package com.ibm.pscan.mapreduce;
import com.ibm.pscan.type.Edge;

import java.io.IOException;

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



public class InputParaMapReduce {

	
	public static class findInputParaMapper extends Mapper<Edge, DoubleWritable, Text, DoubleWritable> {
		private Text nodeID = new Text();
		
        public void map(Edge key, DoubleWritable value, Context context) throws IOException, InterruptedException {
        	nodeID.set(key.getInputVertex());
        	context.write(nodeID, value);     
        	nodeID.set(key.getNeighbor());
        	context.write(nodeID, value);  
        }
    }
	
	
	public static class findInputParaReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

		private DoubleWritable simi = new DoubleWritable();
		
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {                      
        	boolean first=true;
        	double value = 0.0;
        	for(DoubleWritable val:values){
        		double dVal=Double.parseDouble(val.toString());
        		
        		if(first){
        			value = dVal;
        			first=false;
        		}
        		
        		if(dVal>value){
        			value=dVal;
        		}
        	}
        	simi.set(value);
        	context.write(key, simi);
        	
        }
	}
	
	/*------------------------Configuration of mapperClass and reducerClass-------------------------------------------------------------------------------------------------*/
	public static boolean findInputPara(Configuration conf, String path) throws IOException, ClassNotFoundException, InterruptedException {
		
		Job findInputParaJob = new Job(conf, "get input parameters");
		findInputParaJob.setJarByClass(InputParaMapReduce.class);
		findInputParaJob.setMapperClass(findInputParaMapper.class);	 
		findInputParaJob.setCombinerClass(findInputParaReducer.class);
		findInputParaJob.setReducerClass(findInputParaReducer.class);
		findInputParaJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		findInputParaJob.setInputFormatClass(SequenceFileInputFormat.class);
		findInputParaJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		findInputParaJob.setMapOutputKeyClass(Text.class); 
		findInputParaJob.setMapOutputValueClass(DoubleWritable.class); 
	    
		findInputParaJob.setOutputKeyClass(Text.class);
		findInputParaJob.setOutputValueClass(DoubleWritable.class);
	    
	    FileInputFormat.addInputPath(findInputParaJob, new Path(path+"/"+"pcss"));
	    FileOutputFormat.setOutputPath(findInputParaJob, new Path(path+"/"+"inputPara"));
	    
	    return findInputParaJob.waitForCompletion(true);		
		
	}

}
