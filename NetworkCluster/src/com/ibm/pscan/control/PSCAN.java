package com.ibm.pscan.control;

import org.apache.hadoop.conf.Configuration;

import com.ibm.pscan.mapreduce.AdListMapReduce;
import com.ibm.pscan.mapreduce.ClusterMapReduce;
import com.ibm.pscan.mapreduce.LPCCMapReduce;
import com.ibm.pscan.mapreduce.PCSSMapReduce;
import com.ibm.pscan.mapreduce.PCSStoLPCCMapReduce;

public class PSCAN {
	
	private static double thresHold=0.7; //cut off the edges with the structural similarity less than threshold
	
	/**
	 * PSCAN Algorithm
	 */
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		
		//get the adjacency list, which will be the input of PCSS
		AdListMapReduce adListMapReduce=AdListMapReduce.getInstance();
		adListMapReduce.adList(conf);
		
		//calculate the structural similarity and cutting the edges whose similarity less than thresHold
		PCSSMapReduce pcssMapReduce=PCSSMapReduce.getInstance(thresHold);
		pcssMapReduce.PCSS(conf);
		
		//change the output format of PCSS suitable for the input format of LPCC
		PCSStoLPCCMapReduce pcssToLPCCMapReduce=PCSStoLPCCMapReduce.getInstance();
		pcssToLPCCMapReduce.formateToLPCC(conf);
		
		//Calculate the LPCC to get the cluster result
		LPCCMapReduce lpccMapReduce=LPCCMapReduce.getInstance();
		lpccMapReduce.LPCC(conf);
		
		//Get the members of each cluster
		ClusterMapReduce clusterMapReduce=ClusterMapReduce.getInstance();
		clusterMapReduce.cluster(conf);
		
		HubsFinder.findHubs();
		
		
	}
	
	
}
