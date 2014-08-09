package com.ibm.pscan.util;

public class IOPath {

	private static final String BASE_PATH = "/Users/Nancy/Documents/Java/NetworkCluster/";
	
	//For GetParticipant.java
	public static final String GETPAR_INPUT=BASE_PATH+"ori/Messages.csv";
	public static final String GETPAR_OUTPUT=BASE_PATH + "input/userRelation2.txt";
	
	//For ADListMapReduce.java
	public static final String ADLIST_INPUT=BASE_PATH+"input";
	public static final String ADLIST_NEIGHBOR_OUTPUT=BASE_PATH+"output/output";
	public static final String ADLIST_RELATION_OUTPUT=BASE_PATH+"output/output2-relationship";
	public static final String ADLIST_OUTPUT=BASE_PATH+"output/output1-adjacencyList";
		
	//For PCSSMapReduce.java
	public static final String PCSS_INPUT=BASE_PATH+"output/output1-adjacencyList";
	public static final String PCSS_OUTPUT=BASE_PATH+"output/output2-PCSS";
	
	//For PCSStoLPCCMapReduce.java
	public static final String PTOL_INPUT=BASE_PATH+"output/output2-PCSS";
	public static final String PTOL_OUTPUT=BASE_PATH+"output/output3-PtoL";
	
	//For LPCCMapReduce.java
	public static final String LPCC_INPUT=BASE_PATH+"output/output3-PtoL";
	public static final String LPCC_OUTPUT=BASE_PATH+"output/output4-LPCC";
	public static final String LPCC_OUTPUT2=BASE_PATH+"output/output5-LPCC";
	public static final String LPCC_ADLIST_INPUT=BASE_PATH+"output/output1-adjacencyList";
	
	//For ClusterMapReduce.java
	public static final String CLUSTER_INPUT=BASE_PATH+"output/output4-LPCC";
	public static final String CLUSTER_OUTPUT=BASE_PATH+"output/output5-Clusters";
	
	
}
