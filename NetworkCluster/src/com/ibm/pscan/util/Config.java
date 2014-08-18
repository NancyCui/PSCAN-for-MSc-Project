package com.ibm.pscan.util;

/**
 * Settings
 * 
 * @author Ningxin
 */

public class Config {
	
	//tokens for download data from yammer
	private static String since="2014-07-15T00:00:00";
	private static String model="Message";
	private static String access_token="6JeMC7oECscFdMAkSYD6w";
	
	public static final String YAMMER_PATH="https://www.yammer.com/api/v1/export?since="+since+"&model="+model+"&access_token="+access_token;
	//BASE_PATH for the output files and input files
	public static final String BASE_PATH="/Users/Nancy/Documents/Java/NetworkCluster/";
	//thresHold for PSCAN
	public static final double thresHold=0.7;
	//Upload to Azure
	public static final String storageConnectionString =
		        "DefaultEndpointsProtocol=http;"
		        + "AccountName=ibmyammer;"
		        + "AccountKey=reNtloLAIW9zCqudTCHay2PSMToNM5TLtN010F9lwwkm59xPjk7oWYgXDVmf/cVB931QA8GORQ0gJilqg0vULQ==";
	
	public static final String CONTAINER_NAME="pscan-ori";
	public static final String STORAGE_FILE_NAME="Messages.csv";
	
}
