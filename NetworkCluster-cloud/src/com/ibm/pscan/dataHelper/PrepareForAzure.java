package com.ibm.pscan.dataHelper;

import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.io.YammerIO;

public class PrepareForAzure {
	//unzip file
	private String fileName;
	private String filePath;
	//upload to azure
	private String inputFilePath;
	private String containerName;
	private String storageFileName;	
	
	public PrepareForAzure(String fileName, String filePath, String inputFilePath,String containerName,String storageFileName){
		this.fileName=fileName;
		this.filePath=filePath;
		this.inputFilePath=inputFilePath;
		this.containerName=containerName;
		this.storageFileName=storageFileName;
	}
	
	public void prepare(){
		//download the data from yammer
		YammerIO.downloadMessage(); 
		//unzip the downloaded file
		UnZip.unZipFile(fileName, filePath);
		//upload file to azure, parameters: inputFilePath, containterName, fileName
		AzureIO.uploadToAzure(inputFilePath,containerName,storageFileName);
	}
}
