package com.ibm.pscan.util;

/**
 * Settings
 * 
 * @author Ningxin
 */

public class Config {
	
	//tokens for download data from yammer
	private static String since="2014-07-15T00:00:00";
	private static String model="Message&Users";
	private static String access_token="6JeMC7oECscFdMAkSYD6w";
	
	public static final String YAMMER_PATH="https://www.yammer.com/api/v1/export?since="+since+"&model="+model+"&access_token="+access_token;
	
	//BASE_PATH for the output files and input files
	public static final String BASE_PATH="pscan/";

	//Upload to Azure
	public static final String storageConnectionString =
		        "DefaultEndpointsProtocol=http;"
		        + "AccountName=ibmyammer;"
		        + "AccountKey=reNtloLAIW9zCqudTCHay2PSMToNM5TLtN010F9lwwkm59xPjk7oWYgXDVmf/cVB931QA8GORQ0gJilqg0vULQ==";
	
	public static final String CONTAINER_NAME="ningxin";
	public static final String STORAGE_FILE=BASE_PATH+"ori/";
	public static final String STORAGE_FILE_NAME=STORAGE_FILE+"Messages.csv";
	public static final String STORAGE_FILE_NAME_USER=STORAGE_FILE+"Users.csv";
	
	//The path for storing the input file of PSCAN
	public static final String FORMAT_INPUT="example/pscan";
	
	//The path for FormatInputTwo	
	public static final String FORMAT_INPUT_TWO="wasb:///"+FORMAT_INPUT;
}
