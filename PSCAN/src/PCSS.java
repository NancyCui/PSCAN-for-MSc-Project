import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.KeyFieldBasedPartitioner;


/**
 * Calculate the structural similarity of edges
 * The output is the edge and the structural similarity of the edge
 * @author Ningxin
 */
public class PCSS {
	
	//cut off the edges with the structural similarity less than threshold
	private static Double thresHold=0.7; 
	
	/**
	 * Key is the input vertex, value is the adjacency list of the input vertex
	 * Key' is the edge connecting the input vertex and its neighbor
	 * Value' is the adjacency list of the input vertex
	 * @author Ningxin
	 *
	 */
	public static class doPCSSMapper extends Mapper<Text, Text, Edge, Text> {
		
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        	String formatedValue = value.toString().replaceAll("	", "");
        	String[] values=formatedValue.split(",");
        	ArrayList<ArrayList<String>> keyArray=new ArrayList<ArrayList<String>>();       
        	for(int i=0;i<values.length;i++){
        		if(i!=0){
	        		ArrayList<String> edge=new ArrayList<String>();
	        		edge.add(key.toString());
	        		edge.add(values[i]);
	        		keyArray.add(edge);
        		}
        	}
        	for(int i=0;i<keyArray.size();i++){
        		String inputVertex=keyArray.get(i).get(0).toString();
        		String neighbor=keyArray.get(i).get(1).toString();
        		Edge edge=new Edge(inputVertex,neighbor);
        		context.write(edge, value);
        	}
        	
        	          
        }
    }
	
	/**
	 * Key' is the edge connecting the input vertex and its neighbor
	 * Value' is the structural similarity of the edge 
	 * Only output the edges whose structural similarity > = threshold
	 * @author Ningxin
	 *
	 */
	public static class doPCSSReducer extends Reducer<Edge, Text, Text, DoubleWritable> {;
		
		private DoubleWritable strSimVal=new DoubleWritable();
		
        public void reduce(Edge key, Iterable<Text> values, Context context) throws IOException, InterruptedException {                      
        	String inputAdjacencyList = "";
        	String neighborAdjacencyList="";
        	double structuralSimilarity;
        	int i=0;
        	for(Text val:values){
        		if(i==0){
        			inputAdjacencyList+= val.toString().replaceAll("	", "");
        			i++;
        		}
        		else{
        			neighborAdjacencyList+= val.toString().replaceAll("	", "");
        		}
        	}
        	String inputList[]=inputAdjacencyList.split(",");
        	String neighborList[]=neighborAdjacencyList.split(",");
        	double common=0;
        	for(int inputIndex=0; inputIndex<inputList.length;inputIndex++){
        		for(int neighborIndex=0; neighborIndex<neighborList.length;neighborIndex++){
        			if(inputList[inputIndex].compareTo(neighborList[neighborIndex])==0){
        				common++;
        				break;
        			}
        		}
        	}
        	structuralSimilarity=(common)/Math.sqrt(inputList.length*neighborList.length);
        	strSimVal.set(structuralSimilarity);
        	if(structuralSimilarity>=thresHold){
        		context.write(new Text(key.getEdge()), strSimVal);
        	}
        	 
        }
	}
	
	
/*------------------------Configuration of mapperClass and reducerClass-------------------------------------------------------------------------------------------------*/
	private static boolean doPCSS(Configuration conf, String input,
			String output) throws IOException, ClassNotFoundException, InterruptedException {
		
		Job doPCSSJob = new Job(conf, "get adjancency list");
		doPCSSJob.setJarByClass(AdjacencyList.class);
		doPCSSJob.setMapperClass(doPCSSMapper.class);	    
		doPCSSJob.setReducerClass(doPCSSReducer .class);
		doPCSSJob.setPartitionerClass(KeyFieldBasedPartitioner.class);
	    
		doPCSSJob.setInputFormatClass(KeyValueTextInputFormat.class);
		//doPCSSJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		doPCSSJob.setMapOutputKeyClass(Edge.class); 
		doPCSSJob.setMapOutputValueClass(Text.class); 
	    
		doPCSSJob.setOutputKeyClass(Text.class);
		doPCSSJob.setOutputValueClass(DoubleWritable.class);
	    
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
