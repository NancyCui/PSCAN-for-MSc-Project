package com.ibm.pscan.control;

import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.util.Config;



/**
 * Upload jar to yammer
 * 
 * @author Ningxin
 */

public class UploadJar {

	private static String inputFilePath="/Users/Nancy/Desktop/pscan.jar";
	private static String containerName=Config.CONTAINER_NAME;
	private static String storageFileName="example/jars/pscan.jar";	
//	private static String inputFilePath="/Users/Nancy/Desktop/userRelation.txt";
//	private static String containerName=Config.CONTAINER_NAME;
//	private static String storageFileName="example/pscan/userRelation.txt";	
	
	public static void main(String[] args){
		AzureIO.uploadToAzure(inputFilePath,containerName,storageFileName);
	}
}
