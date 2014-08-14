package com.ibm.pscan.control;

import org.apache.hadoop.conf.Configuration;

import com.ibm.pscan.dataHelper.HubsFinder;
import com.ibm.pscan.mapreduce.AdListMapReduce;
import com.ibm.pscan.mapreduce.ClusterMapReduce;
import com.ibm.pscan.mapreduce.LPCCMapReduce;
import com.ibm.pscan.mapreduce.PCSSMapReduce;
import com.ibm.pscan.mapreduce.PCSStoLPCCMapReduce;
import com.ibm.pscan.util.IOPath;

public class PSCAN {
	
	private static double thresHold=0.8; //cut off the edges with the structural similarity less than threshold
	
	/**
	 * PSCAN Algorithm
	 */
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		
		//get the adjacency list, which will be the input of PCSS
		//input: conf, inputFile, outputFile
		AdListMapReduce adListMapReduce=AdListMapReduce.getInstance();
		adListMapReduce.adList(conf, IOPath.ADLIST_INPUT, IOPath.ADLIST_OUTPUT);
		
		//calculate the structural similarity and cutting the edges whose similarity less than thresHold
		PCSSMapReduce pcssMapReduce=PCSSMapReduce.getInstance(thresHold);
		pcssMapReduce.PCSS(conf,IOPath.PCSS_INPUT, IOPath.PCSS_OUTPUT);
		
		//change the output format of PCSS suitable for the input format of LPCC
		PCSStoLPCCMapReduce pcssToLPCCMapReduce=PCSStoLPCCMapReduce.getInstance();
		pcssToLPCCMapReduce.formateToLPCC(conf,IOPath.PTOL_INPUT, IOPath.PTOL_OUTPUT);
		
		//Calculate the LPCC to get the cluster result
		LPCCMapReduce lpccMapReduce=LPCCMapReduce.getInstance();
		lpccMapReduce.LPCC(conf,IOPath.LPCC_INPUT, IOPath.LPCC_OUTPUT);
		
		//Get the members of each cluster
		ClusterMapReduce clusterMapReduce=ClusterMapReduce.getInstance();
		clusterMapReduce.cluster(conf,IOPath.CLUSTER_INPUT, IOPath.CLUSTER_OUTPUT);
		
		HubsFinder.findHubs();
		
	}
	
}
