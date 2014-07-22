import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.KeyFieldBasedPartitioner;

/**
 * Achieve the adjacency list of each node
 * key: v1, v2
 * value: adjacency list of v1
 * @author Ningxin
 *
 */
public class AdjancencyList {
	
	/**
	 * Delete the duplicate lines in the input files.
	 * @author Ningxin
	 */
	public static class deleteDuplicationMapper extends Mapper<Text, Text, Text, Text> {
    	private Text userRelationID=new Text();
 
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        	userRelationID.set(key);
        	context.write(userRelationID, new Text(""));           
        }
    }
	
	/**
	 * Delete the duplicate items and for each key(fromID, toID)
	 * Write not only the key(fromID, toID) but also key(toID, fromID)
	 * @author Ningxin
	 */
	public static class deleteDuplicationReducer extends Reducer<Text, Text, Text, Text> {
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
	public static class getAdjancencyListMapper extends Mapper<Text, Text, Text, Text> {
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {			
			context.write(value,key);
		}
	}
	
	/**
	 * (value,key)->same key together
	 * Ensure the type is consistent in MapperClass and ReducerClass
	 */
	public static class getAdjancencyListReducer extends Reducer<Text, Text, Text, Text> {
	    public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException {
	        
	    	String csv = key.toString().replaceAll("	", "");
	    	for (Text val : values){
	            if (csv.length() > 0) {
	            	csv += ",";
	            }
	            csv += val.toString();
	        }
	        context.write(key, new Text(csv));
	    }
	}	
	
	
	/**
	 * Set the MapperClass and ReducerClass for the deleteDuplicationJob
	 * Job is used to delete the duplicate items in the input files
	 * @param conf
	 * @param input
	 * @param output
	 * @return deleteDuplicationJob.waitForCompletion(true)
	 * @throws Exception
	 */
	private static boolean deleteDuplication(Configuration conf, String input,
			String output) throws Exception {
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", "\n");
		
		Job deleteDuplicationJob = new Job(conf, "delete duplicate relationship");
		deleteDuplicationJob.setJarByClass(AdjancencyList.class);
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
	

	private static boolean getAdjancencyList(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", ",");
		Job getAdjancencyListJob = new Job(conf, "get adjancency list");
		getAdjancencyListJob.setJarByClass(AdjancencyList.class);
		getAdjancencyListJob.setMapperClass(getAdjancencyListMapper.class);	    
		getAdjancencyListJob.setReducerClass(getAdjancencyListReducer .class);
		getAdjancencyListJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		getAdjancencyListJob.setInputFormatClass(KeyValueTextInputFormat.class);
		
		getAdjancencyListJob.setMapOutputKeyClass(Text.class); 
		getAdjancencyListJob.setMapOutputValueClass(Text.class); 
	    
		getAdjancencyListJob.setOutputKeyClass(Text.class);
		getAdjancencyListJob.setOutputValueClass(Text.class);
	    
	    FileInputFormat.addInputPath(getAdjancencyListJob, new Path(input));
	    FileOutputFormat.setOutputPath(getAdjancencyListJob, new Path(output));
	    
	    return getAdjancencyListJob.waitForCompletion(true);		
		
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		//delete the duplicate records in the original file
		boolean successful=deleteDuplication(conf,args[0], args[1]);
		//get the adjustList of each node
		if(successful){
			getAdjancencyList(conf,args[1], "output2");
		}
		System.exit(0);
	}



	
	
}
