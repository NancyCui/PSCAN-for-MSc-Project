import io.ArrayListWritable;
import io.Edge;

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
	
	
	@SuppressWarnings("rawtypes")
	public static class getAdListReducer extends Reducer<Text, Text, Text, ArrayListWritable> {
		private Text initStatus= new Text();

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {   
			ArrayListWritable<ArrayListWritable<Text>> strInfo=new ArrayListWritable<ArrayListWritable<Text>>();
			ArrayListWritable<Text> status=new ArrayListWritable<Text>();
			ArrayListWritable<Text> label=new ArrayListWritable<Text>();
			ArrayListWritable<Text> adjList=new ArrayListWritable<Text>();
			
			//initial the status
			initStatus.set("activated");
    		status.add(initStatus);
    		
    		//initial the label as its ownID
    		label.add(key); 
    		
    		//get the adjacency list of the vertex			
    		adjList.add(key);    
    		for(Text val:values){
    			String value=val.toString();
        		adjList.add(new Text(value));
        		System.out.println("adjList: "+adjList.toString());
    		}
    		
    		strInfo.add(status);
    		strInfo.add(label);
    		strInfo.add(adjList);
    		
    		context.write(key, strInfo);
    	}

	}
	
	private static boolean getAdList(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		
		Job getAdListJob = new Job(conf, "get adjancency list");
		getAdListJob.setJarByClass(PCSStoLPCC.class);
		getAdListJob.setMapperClass(getAdListMapper.class);	
		getAdListJob.setReducerClass(getAdListReducer.class);
		getAdListJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		getAdListJob.setInputFormatClass(SequenceFileInputFormat.class);
		//getAdListJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		getAdListJob.setMapOutputKeyClass(Text.class); 
		getAdListJob.setMapOutputValueClass(Text.class); 
	    
		getAdListJob.setOutputKeyClass(Text.class);
		getAdListJob.setOutputValueClass(ArrayListWritable.class);
	    
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
