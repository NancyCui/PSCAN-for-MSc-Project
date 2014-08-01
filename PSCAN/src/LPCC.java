import io.ArrayListWritable;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
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



public class LPCC {
	private static String output="output4-LPCC";
	private static int loopCount=0;
	private static boolean iterateLPCC=true; 
	
	/**
	 * Input: <Text, ArrayListWritable>
	 * key is the input vertex ID
	 * value is the structure information of the input vertex
	 * Output: <Text, ArrayListWritable>
	 * key' is a vertex ID
	 * value' is the label or the structure information of the input vertex
	 * @author Ningxin
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
	 * @author Ningxin
	 *
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
				iterateLPCC=true;
			}
			else{
				strInfo.get(0).get(0).set(new Text("inactivated"));
			}
			context.write(key,strInfo);						
		}

	}	
	
/*------------------------Configuration of mapperClass and reducerClass-------------------------------------------------------------------------------------------------*/
	private static boolean doLPCC(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		
		iterateLPCC=false;
		
		Job doLPCCJob = new Job(conf, "cluster");
		doLPCCJob.setJarByClass(LPCC.class);
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
	
/*------------------------Main Method-------------------------------------------------------------------------------------------------*/		
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		boolean successLPCC=true;
		String outputInter="output5-LPCC";
		FileSystem fs = FileSystem.get(conf);
		
		/*
		 * iterate the Map and the Reduce process until all status is in-activated
		 */
		while(successLPCC){
			while(iterateLPCC){
				if(loopCount==0){
					successLPCC=doLPCC(conf,args[0], args[1]);
					loopCount++;
				}
				else if((loopCount%2)==0&&loopCount!=0){
					fs.delete(new Path(args[1]), true);
					successLPCC=doLPCC(conf,outputInter, args[1]);
					loopCount++;
				}
				else{
					if(fs.exists(new Path(outputInter))){
						fs.delete(new Path(outputInter),true);
					}
					successLPCC=doLPCC(conf,args[1], outputInter);
					loopCount++;
				}
			}
			
			/*
			 * format the name of the output file
			 */
			if(loopCount%2==0&&loopCount!=0){
				fs.delete(new Path(args[1]),true);
				fs.rename(new Path(outputInter), new Path(output));
			}
			else{
				fs.rename(new Path(args[1]), new Path(output));
				if(fs.exists(new Path(outputInter))){
					fs.delete(new Path(outputInter),true);
				}
			}
			System.exit(0);	
		
			
		}		
		
	}

}
