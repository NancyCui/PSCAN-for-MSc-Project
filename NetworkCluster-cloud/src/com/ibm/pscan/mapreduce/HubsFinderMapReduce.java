package com.ibm.pscan.mapreduce;


import com.ibm.pscan.type.ArrayListWritable;
import com.ibm.pscan.util.Config;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.io.SequenceFileIO;

/**
 * Find the hubs and outliners of the cluster
 * Start from "findHubs()"
 * 
 * @author Ningxin
 */

public class HubsFinderMapReduce {
	
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
	
	/**
	 * Put each clusterID and cluster member into hashmap
	 * The key is the clusterID 
	 * The value is an arraylist which contains the nodeID of each member
	 * 
	 * @param output
	 */
	private static Map<String, ArrayList<String>> getClusterMembers(
			ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> output) {
		//store the clusterID and memberID
		Map<String,ArrayList<String>> clusterMember= new HashMap<String,ArrayList<String>>();		
		for(ArrayListWritable<ArrayListWritable<Text>> o: output){
			String ownID=o.get(0).get(0).toString();
			//If the node belongs to a cluster
			if(o.get(1).get(0).toString().equals("members")){
				String clusterID=o.get(2).toString();
				clusterMember=putInHashMap(clusterMember,clusterID, ownID);
			}
			//If the node is either a "hubs" or "outliners"
			else{
				if(o.get(2).get(0).toString().equals("hubs")){
					clusterMember=putInHashMap(clusterMember,"hubs", ownID);
				}
				else{
					clusterMember=putInHashMap(clusterMember,"outliners", ownID);
				}
			}
		}
		return clusterMember;		
	}

	/**
	 * Put key value pair to the hashmap
	 * 
	 * @param clusterMember clusterID clusterMember
	 */
	private static Map<String, ArrayList<String>> putInHashMap(
		Map<String, ArrayList<String>> clusterMember, String clusterID, String ownID) {
		
		if(clusterMember.containsKey(clusterID)){
			ArrayList<String> value=clusterMember.get(clusterID);
			value.add(ownID);
			Collections.sort(value);
			clusterMember.put(clusterID, value);
		}
		else{
			ArrayList<String> value=new ArrayList<String>();
			value.add(ownID);
			clusterMember.put(clusterID, value);
		}
		return clusterMember;
	}

	public static void findHubs(Configuration conf, String path) throws IOException {

		String fileVertexs=path+"/"+"lpccAfterOutput";
		String fileCluster=path+"/"+"clusters";
		
		ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> vertexs = new ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>>();
	    FileSystem fsVertex = FileSystem.get(URI.create(fileVertexs), conf);
	    FileStatus fileList[] = fsVertex.listStatus(new Path(fileVertexs));
	    int size = fileList.length;
		for(int i = 0; i < size; i++){
			if( fileList[i].getPath().toString().contains("part")){
				ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> v=SequenceFileIO.readSequenceFileTAA(fsVertex, fileList[i].getPath() , conf); 
				for(int j=0;j<v.size();j++){
					vertexs.add(v.get(j));
				}
			}
		}
		
		fsVertex.close();
		
		ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> clusters= new ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>>();
	    FileSystem fsCluster = FileSystem.get(URI.create(fileCluster), conf);
	    FileStatus fileListCluster[] = fsCluster.listStatus(new Path(fileCluster));
	    int sizeCluster = fileListCluster.length;
		for(int i = 0; i < sizeCluster; i++){
			if( fileListCluster[i].getPath().toString().contains("part")){
				ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> c=SequenceFileIO.readSequenceFileTA(fsCluster, fileListCluster[i].getPath() , conf); 
				for(int j=0;j<c.size();j++){
					clusters.add(c.get(j));
				}
			}
		}
		
		fsCluster.close();
	    
	    
	    //find Hubs and outliners  
	    ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> output=findHubs(vertexs, clusters);
	    
	    Map<String,ArrayList<String>> clusterMember=getClusterMembers(output);
	    System.out.println(clusterMember);
	    CsvFileIO.writeToCsv("result.csv",clusterMember);
	   
//		String inputFilePath="result.csv";
//		String containerName=Config.CONTAINER_NAME;
//		String storageFileName=path+"/"+"result.csv";		
//
//		AzureIO.uploadToAzure(inputFilePath,containerName,storageFileName);
		


	  }







}
