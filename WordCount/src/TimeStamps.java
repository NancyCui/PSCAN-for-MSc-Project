import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
	
public class TimeStamps {
	
	public static class Map extends Mapper<Text, Text, Text, IntWritable> {
	    private final static IntWritable one = new IntWritable(1);
	    private Text word = new Text();
	 
	    public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
		    System.out.println(key.toString()+" "+value.toString());
	    	String line = value.toString();
		    StringTokenizer tokenizer = new StringTokenizer(line, " \t\n\r\f,.:;?![]'");
		    while (tokenizer.hasMoreTokens()) {
		    	word.set(tokenizer.nextToken().toLowerCase());
		    	context.write(word, one);
		    }
	    }
	} 
	
	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            
            context.write(key, new IntWritable(sum));
        }
    }
	
    public static void main(String[] args) throws Exception {
	    	//holding key/value configuration parameters
	    	Configuration conf = new Configuration();
	        
	        Job job = new Job(conf, "TimeStamps");
	        job.setJarByClass(TimeStamps.class);
	        job.setOutputKeyClass(Text.class);
	        job.setOutputValueClass(IntWritable.class);
	 
	        job.setMapperClass(Map.class);
	        job.setCombinerClass(Reduce.class);
	        job.setReducerClass(Reduce.class);
	 
	        job.setInputFormatClass(KeyValueTextInputFormat.class);
	        job.setOutputFormatClass(TextOutputFormat.class);
	        
	        //Change the separator character
	        job.getConfiguration().set("mapred.textoutputformat.separator", ",");
	              
	        //path object to encode file, directory names
	        FileInputFormat.addInputPath(job, new Path(args[0]));
	        FileOutputFormat.setOutputPath(job, new Path(args[1]));
	 
	        job.waitForCompletion(true);
	    }        
	


}
