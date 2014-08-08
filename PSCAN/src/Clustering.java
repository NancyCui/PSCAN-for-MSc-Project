import io.ArrayListWritable;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
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



public class Clustering {
	
	public static class convertMapper extends Mapper<Text, ArrayListWritable<ArrayListWritable<Text>>, Text, Text> {
 
        public void map(Text key, ArrayListWritable<ArrayListWritable<Text>> value, Context context) throws IOException, InterruptedException {
        	ArrayListWritable<Text> label=new ArrayListWritable<Text>(value.get(1));
        	Text clusterName=label.get(0);
        	context.write(clusterName, key);           
        }
    }
	
	
	public static class convertReducer extends Reducer<Text, Text, Text, ArrayListWritable<Text>> {

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {                      
        	ArrayListWritable<Text> output=new ArrayListWritable<Text>();
        	for(Text val:values){
        		output.add(new Text(val.toString()));
        	}
        	context.write(key, output);
        	
        }
	}
	

	private static boolean convert(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		
		Job convertJob = new Job(conf, "convert");
		convertJob.setJarByClass(Clustering.class);
		convertJob.setMapperClass(convertMapper.class);	    
		convertJob.setReducerClass(convertReducer .class);
		convertJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		convertJob.setInputFormatClass(SequenceFileInputFormat.class);
		convertJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		convertJob.setMapOutputKeyClass(Text.class); 
		convertJob.setMapOutputValueClass(Text.class); 
	    
		convertJob.setOutputKeyClass(Text.class);
		convertJob.setOutputValueClass(ArrayListWritable.class);
	    
	    FileInputFormat.addInputPath(convertJob, new Path(input));
	    FileOutputFormat.setOutputPath(convertJob, new Path(output));
	    
	    return convertJob.waitForCompletion(true);		
		
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();		
		boolean successConvert=convert(conf,args[0], args[1]);
		if(successConvert){
			System.exit(0);
		}		
		
	}
}
