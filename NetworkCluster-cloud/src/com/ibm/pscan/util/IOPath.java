package com.ibm.pscan.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IOPath {
	
	private static DateFormat dateFormat = new SimpleDateFormat("YYYY_MM_dd-HH.mm.ss");
	private static Calendar cal = Calendar.getInstance();
	private static String DATE=dateFormat.format(cal.getTime());

	private static final String BASE_PATH=Config.BASE_PATH;
	private static final String OUTPUT_BASE_PATH = BASE_PATH+"output_"+DATE+"/";
	
	/**
	 * For YammerIO.java & Unzip.java & YammerToAzure.java
	 */
	public static final String ZIP_BASE=Config.BASE_PATH+"/yammer";
	public static final String ZIP_FILE=ZIP_BASE+"/message.zip";
	public static final String UNZIP_FILE=ZIP_BASE+"/message/";;
	public static final String INPUT_FILE_PATH=UNZIP_FILE+"/Messages.csv";
	
	
	/**
	 * Input and Output Files for finding participant
	 */
	//For GetParticipant.java
	public static final String GETPAR_INPUT=BASE_PATH+"ori/Messages.csv";
	public static final String GETPAR_OUTPUT=BASE_PATH + "input/userRelation.txt";
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
	
	
}
