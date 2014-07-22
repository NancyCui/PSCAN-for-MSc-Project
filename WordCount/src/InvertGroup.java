import java.io.IOException;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.mapreduce.*;



public class InvertGroup{

	/*
	 * Invert the key value pair-> (key, value)->(value, key)
	 */
	public static class MapClass extends Mapper<Text, Text, Text, Text> {
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {			
			System.out.println("Key: "+ key+" Values: "+ value);
			context.write(value,key);
		}
	}
	
	/*
	 * (value,key)->same key together
	 * Ensure the type is consistent in Mapper and Reducer
	 * Output is the list of citing patents
	 */
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
	    public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException {
	        
	    	String csv = "";
	    	for (Text val : values){
	            if (csv.length() > 0) {
	            	csv += ",";
	            }
	            csv += val.toString();
	        }
	        context.write(key, new Text(csv));
	    }
	}
	
	
	/*
	 * Job configuration
	 */
    public static void main(String[] args) throws Exception {
    	Configuration conf = new Configuration();
    	
    	//separate the key value pair by ","
    	conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", ",");
        
        Job job = new Job(conf, "MyJob");
        job.setJarByClass(InvertGroup.class);
        
        //k2, v2
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
 
        job.setMapperClass(MapClass.class);
        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);
 
        //k1, v1
        job.setInputFormatClass(KeyValueTextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
       
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
    	job.waitForCompletion(true);
    }
}
	

