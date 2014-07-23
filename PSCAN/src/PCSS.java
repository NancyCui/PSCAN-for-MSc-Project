import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.KeyFieldBasedPartitioner;

/**
 * Calculate the structural similarity of edges
 * The output is the edge and the structural similarity of the edge
 * @author Ningxin
 */
public class PCSS {
	
	/**
	 * Key is the input vertex, value is the adjacency list of the input vertex
	 * Key' is the edge connecting the input vertex and its neighbor
	 * Value' is the adjacency list of the input vertex
	 * @author Ningxin
	 *
	 */
	public static class doPCSSMapper extends Mapper<Text, Text, ArrayWritable, Text> {
		
		
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        	String newValue = value.toString().replaceAll("	", "");
        	          
        }
    }
	
	public static class doPCSSReducer extends Reducer<ArrayWritable, Text, ArrayWritable, Text> {

        public void reduce(ArrayWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {                      
        	context.write(key, new Text("")); 
        }
	}
	
	private static boolean doPCSS(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		//conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", "	");
		Job doPCSSJob = new Job(conf, "get adjancency list");
		doPCSSJob.setJarByClass(AdjacencyList.class);
		doPCSSJob.setMapperClass(doPCSSMapper.class);	    
		doPCSSJob.setReducerClass(doPCSSReducer .class);
		doPCSSJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		doPCSSJob.setInputFormatClass(KeyValueTextInputFormat.class);
		doPCSSJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		doPCSSJob.setMapOutputKeyClass(Text.class); 
		doPCSSJob.setMapOutputValueClass(Text.class); 
	    
		doPCSSJob.setOutputKeyClass(ArrayWritable.class);
		doPCSSJob.setOutputValueClass(Text.class);
	    
	    FileInputFormat.addInputPath(doPCSSJob, new Path(input));
	    FileOutputFormat.setOutputPath(doPCSSJob, new Path(output));
	    
	    return doPCSSJob.waitForCompletion(true);		
		
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();		
		boolean successPCSS=doPCSS(conf,args[0], args[1]);
		if(successPCSS){
			System.exit(0);
		}		
		
	}
}
