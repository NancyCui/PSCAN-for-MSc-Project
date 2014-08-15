package com.ibm.pscan.dataHelper;
import com.ibm.pscan.type.ArrayListWritable;
import com.ibm.pscan.util.IOPath;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import com.ibm.pscan.io.SequenceFileIO;

/**
 * Find the hubs and outliners of the cluster
 * 
 * @author Ningxin
 */

public class HubsFinder {
	
	private static int thresholdForHub=2;

	/**
	 * Find if the node is hubs, outliners or members
	 * 
	 * @param vertexs
	 * @param clusters
	 * @return 
	 */
	private static ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> findHubs(ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> vertexs, ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> clusters){
		for(ArrayListWritable<ArrayListWritable<Text>> vertex: vertexs){
			String tag=vertex.get(1).get(0).toString();
			if(tag.equals("adList")){
				ArrayListWritable<Text> clusterNo=findCluster(vertex, clusters);
				if(clusterNo.size()>thresholdForHub){					
					vertex.get(2).get(0).set("hubs");
					vertex.remove(1);
					vertex.add(1,clusterNo);
				}
				else{
					vertex.get(2).get(0).set("outliners");
					vertex.remove(1);
					vertex.add(1,clusterNo);
				}
			}
			else{
				vertex.get(1).get(0).set("members");
			}
		}
		return vertexs;
	}
	



	/**
	 * find which clusters the nodes are connected to
	 * Add the clusterID to the return value
	 * 
	 * @param vertex
	 * @param clusters
	 * @return clusterNo
	 */
	private static ArrayListWritable<Text> findCluster(
			ArrayListWritable<ArrayListWritable<Text>> vertex, ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> clusters) {
		
		//Store the clusterID
		ArrayListWritable<Text> clusterNo=new ArrayListWritable<Text>();		
		ArrayListWritable<Text> adList=vertex.get(3);
		/*
		 * For each cluster, judge if the node in the adjacency List is in the cluster
		 * If yes, add the cluster ID to the output "ArrayListWritable<Text> clusterNo"
		 * and break the loop to go to next cluster
		 * Otherwise, chech the next node in the adjacency List to see if it is in the cluster
		 */
		for(ArrayListWritable<ArrayListWritable<Text>> cluster: clusters){
			//get the node in the cluster
			Text clusterID=cluster.get(0).get(0);
			ArrayListWritable<Text> clusterNode=cluster.get(1);
			for(Text adNode: adList){
				for(Text cNode: clusterNode){
					String nodeID=cNode.toString();
					if(adNode.toString().equals(nodeID)){
						if(!clusterNo.contains(clusterID)){
							clusterNo.add(clusterID);
							break;
						}						
					}
				}
			}
		}
		return clusterNo;
	}
	

	public static void findHubs() throws IOException {

		String fileVertex=IOPath.HUBFINDER_INPUT;
		String fileCluster=IOPath.HUBFINDER_OUTPUT;;
		
	    Configuration conf = new Configuration();
	    FileSystem fsVertex = FileSystem.get(URI.create(fileVertex), conf);
	    Path pathVertex = new Path(fileVertex);
	    FileSystem fsCluster = FileSystem.get(URI.create(fileCluster), conf);
	    Path pathCluster = new Path(fileCluster);
	   
	    
	    //read the sequence file
	    ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> vertexs=SequenceFileIO.readSequenceFileTAA(fsVertex, pathVertex, conf);   
	    ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> clusters=SequenceFileIO.readSequenceFileTA(fsCluster, pathCluster, conf);
	    //find Hubs and outliners  
	    ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> output=findHubs(vertexs, clusters);
	    for(int i=0;i<output.size();i++){
	    	System.out.println(output.get(i).toString());
	    }
	    
	    
	  }
}
