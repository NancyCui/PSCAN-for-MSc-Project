package com.ibm.pscan.control;

import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.util.Config;



/**
 * Upload jar to Azure
 * 
 * @author Ningxin
 */

public class UploadJar {

	//Path for the input file
	private static String inputFilePath="/Users/Nancy/Desktop/pscan.jar";
	
	private static String containerName=Config.CONTAINER_NAME;
	private static String storageFileName="example/jars/pscan.jar";
	
	public static void main(String[] args){
		AzureIO.uploadToAzure(inputFilePath,containerName,storageFileName);
	}
}
