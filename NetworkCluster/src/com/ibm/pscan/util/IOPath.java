package com.ibm.pscan.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IOPath {
	
	private static DateFormat dateFormat = new SimpleDateFormat("HH.mm.ss");
	private static Calendar cal = Calendar.getInstance();
	private static String DATE=dateFormat.format(cal.getTime());

	private static final String BASE_PATH="/Users/Nancy/Documents/Java/NetworkCluster/";
	private static final String OUTPUT_BASE_PATH = "/Users/Nancy/Documents/Java/NetworkCluster/output_"+DATE+"/";

	/**
	 * Input and Output Files for finding participant
	 */
	//For GetParticipant.java
	public static final String GETPAR_INPUT=BASE_PATH+"ori/Messages.csv";
	public static final String GETPAR_OUTPUT=BASE_PATH + "input/userRelation2.txt";
	public static final String GETPAR_OUTPUT_REPLY_BASE=BASE_PATH + "input_reply";
	public static final String GETPAR_OUTPUT_REPLY= GETPAR_OUTPUT_REPLY_BASE + "/reply.txt";
	public static final String GETPAR_OUTPUT_BASE=BASE_PATH + "input_inter";
	public static final String GETPAR_OUTPUT_REPLY_NODUP=GETPAR_OUTPUT_BASE + "/input_reply_nodup";
	public static final String GETPAR_OUTPUT_ID=GETPAR_OUTPUT_BASE + "/input_users";
	
	/**
	 * Input and Output Files for finding input parameter
	 */	
	//For ADListMapReduce.java
	public static final String ADLIST_INPUT_0=BASE_PATH+"input";
	public static final String ADLIST_INPUT_1=GETPAR_OUTPUT_ID;
	public static final String ADLIST_OUTPUT_0=OUTPUT_BASE_PATH+"output0-adjacencyList";
	
	//For PCSSMapReduce.java
	public static final String PCSS_INPUT_0=OUTPUT_BASE_PATH+"output0-adjacencyList";
	public static final String PCSS_OUTPUT_0=OUTPUT_BASE_PATH+"output0-PCSS";
	
	//For InputParaMapReduce.java
	public static final String INPUTPARA_INPUT=OUTPUT_BASE_PATH+"output0-PCSS";
	public static final String INPUTPARA_OUTPUT=OUTPUT_BASE_PATH+"output0-InputPara";

	//For FindInputPara.java
	public static final String FINDINPUTPARA_INPUT=OUTPUT_BASE_PATH+"output0-InputPara/part-r-00000";
	public static final String FINDINPUTPARA_OUTPUT=OUTPUT_BASE_PATH+"output0-Similarity-CSV.csv";
	
	/**
	 * Input and Output Files for PSCAN.java
	 */
	//For ADListMapReduce.java
	public static final String ADLIST_INPUT=BASE_PATH+"input";
	public static final String ADLIST_INPUT2=GETPAR_OUTPUT_ID;
	public static final String ADLIST_NEIGHBOR_OUTPUT=OUTPUT_BASE_PATH+"output";
	public static final String ADLIST_RELATION_OUTPUT=OUTPUT_BASE_PATH+"output2-relationship";
	public static final String ADLIST_OUTPUT=OUTPUT_BASE_PATH+"output1-adjacencyList";
		
	//For PCSSMapReduce.java
	public static final String PCSS_INPUT=OUTPUT_BASE_PATH+"output1-adjacencyList";
	public static final String PCSS_OUTPUT=OUTPUT_BASE_PATH+"output2-PCSS";
	
	//For PCSStoLPCCMapReduce.java
	public static final String PTOL_INPUT=OUTPUT_BASE_PATH+"output2-PCSS";
	public static final String PTOL_OUTPUT=OUTPUT_BASE_PATH+"output3-PtoL";
	
	//For LPCCMapReduce.java
	public static final String LPCC_INPUT=OUTPUT_BASE_PATH+"output3-PtoL";
	public static final String LPCC_OUTPUT=OUTPUT_BASE_PATH+"output4-LPCC";
	public static final String LPCC_OUTPUT2=OUTPUT_BASE_PATH+"output5-LPCC";
	public static final String LPCC_ADLIST_INPUT=OUTPUT_BASE_PATH+"output1-adjacencyList";
	
	//For ClusterMapReduce.java
	public static final String CLUSTER_INPUT=OUTPUT_BASE_PATH+"output4-LPCC";
	public static final String CLUSTER_OUTPUT=OUTPUT_BASE_PATH+"output5-Clusters";	
	
	//For HubesFineder.java
	public static final String HUBFINDER_INPUT=OUTPUT_BASE_PATH+"output4-LPCC/part-r-00000";
	public static final String HUBFINDER_OUTPUT=OUTPUT_BASE_PATH+"output5-Clusters/part-r-00000";
	
}
