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
import org.apache.hadoop.mapreduce.lib.partition.KeyFieldBasedPartitioner;


public class PCSStoLPCC {
	
	public static class getAdListMapper extends Mapper<Edge, DoubleWritable, Text, Text> {
		
        public void map(Edge key, DoubleWritable value, Context context) throws IOException, InterruptedException {
        	context.write(new Text(key.getInputVertex()), new Text(key.getNeighbor()));
        	context.write(new Text(key.getNeighbor()), new Text(key.getInputVertex()));
        }
    }
	
	public static class getAdListReducer extends Reducer<Text, Text, Text, Text> {;
		private Text adjacencyList=new Text();
		
    	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {   
    		String adList=key.toString()+",";
    		for(Text val:values){
    			adList+=val.toString()+",";
    		}
    		adList=adList.substring(0,adList.length()-1);
    		adjacencyList.set(adList);
    		context.write(key, adjacencyList);
    	}

	}
	
	private static boolean getAdList(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		
		Job getAdListJob = new Job(conf, "get adjancency list");
		getAdListJob.setJarByClass(AdjacencyList.class);
		getAdListJob.setMapperClass(getAdListMapper.class);	
		//getAdListJob.setCombinerClass(getAdListReducer.class);	
		getAdListJob.setReducerClass(getAdListReducer.class);
		getAdListJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		getAdListJob.setInputFormatClass(SequenceFileInputFormat.class);
		//doPCSSJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		getAdListJob.setMapOutputKeyClass(Text.class); 
		getAdListJob.setMapOutputValueClass(Text.class); 
	    
		getAdListJob.setOutputKeyClass(Text.class);
		getAdListJob.setOutputValueClass(Text.class);
	    
	    FileInputFormat.addInputPath(getAdListJob, new Path(input));
	    FileOutputFormat.setOutputPath(getAdListJob, new Path(output));
	    
	    return getAdListJob.waitForCompletion(true);		
		
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();		
		boolean successFormat=getAdList(conf,args[0], args[1]);
		if(successFormat){
			System.exit(0);
		}		
		
	}
}
