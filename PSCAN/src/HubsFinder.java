import io.ArrayListWritable;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import fileIO.SequenceFileReader;


public class HubsFinder {
	
	private static int thresholdForHub=1;

	/**
	 * Find if the node is hubs, outliners or members
	 * @param vertexs
	 * @param clusters
	 */
	private static void findHubs(ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> vertexs, ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> clusters){
		for(ArrayListWritable<ArrayListWritable<Text>> vertex: vertexs){
			String tag=vertex.get(1).get(0).toString();
			if(tag.equals("adList")){
				ArrayListWritable<Text> clusterNo=findCluster(vertex, clusters);
				if(clusterNo.size()>thresholdForHub){
					vertex.get(1).get(0).set("hubs");
				}
				else{
					vertex.get(1).get(0).set("outliners");
				}
			}
			else{
				vertex.get(1).get(0).set("members");
			}
		}
	}
	
	
	/**
	 * find which clusters the nodes are connected to
	 * @param vertex
	 * @param clusters
	 * @return clusterNo
	 */
	private static ArrayListWritable<Text> findCluster(
			ArrayListWritable<ArrayListWritable<Text>> vertex, ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> clusters) {
		ArrayListWritable<Text> clusterNo=new ArrayListWritable<Text>();
		
		return clusterNo;
	}
	

	public static void main(String[] args) throws IOException {
		
		String bathPath = "/Users/Nancy/Documents/Java/PSCAN/";
		String fileVertex=bathPath+"output4-LPCC/part-r-00000";
		String fileCluster=bathPath+"output5-Clusters/part-r-00000";
	    Configuration conf = new Configuration();
	    FileSystem fsVertex = FileSystem.get(URI.create(fileVertex), conf);
	    Path pathVertex = new Path(fileVertex);
	    FileSystem fsCluster = FileSystem.get(URI.create(fileCluster), conf);
	    Path pathCluster = new Path(fileCluster);
	    
	    //read the sequence file
	    ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> vertexs=SequenceFileReader.readSequenceFile(fsVertex, pathVertex, conf);   
	    ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> clusters=SequenceFileReader.readSequenceFile(fsCluster, pathCluster, conf);
	    for(int i=0;i<vertexs.size();i++){
	    	System.out.println(vertexs.get(i).toString());
	    }
	    for(int i=0;i<clusters.size();i++){
	    	System.out.println(clusters.get(i).toString());
	    }
	    //find Hubs and outliners  
	    findHubs(vertexs, clusters);
	  }
}
