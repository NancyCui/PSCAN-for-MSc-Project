import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;



public class CitationHistogram{

	/*
	 * Invert the key value pair-> (key, value)->(value, key)
	 */
	public static class MapClass extends Mapper<Text, Text, IntWritable, IntWritable> {
		private final static IntWritable uno = new IntWritable(1);
        private IntWritable citationCount = new IntWritable();
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {			
			citationCount.set(Integer.parseInt(value.toString()));
			context.write(citationCount,uno);
		}
	}
	
	
	/*
	 * Counts the number of citations for each patent 
	 */
	public static class Reduce extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
	    public void reduce(IntWritable key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {	        
	    	int count = 0;
	    	for (IntWritable val : values){
	           count+=val.get();	            
	        }
	        context.write(key, new IntWritable(count));
	    }
	}
	
	/*
	 * Job configuration
	 */
    public static void main(String[] args) throws Exception {
    	Configuration conf = new Configuration();
    	
    	//separate the key value pair by ","
    	conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", ",");
        
        Job job = new Job(conf, "CitationHistogram");
        job.setJarByClass(CitationHistogram.class);
        
        //k2, v2
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
 
        job.setMapperClass(MapClass.class);
        job.setReducerClass(Reduce.class);
 
        //k1, v1
        job.setInputFormatClass(KeyValueTextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
       
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
    	job.waitForCompletion(true);
    }
}
	

