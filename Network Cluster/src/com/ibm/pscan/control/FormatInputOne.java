package com.ibm.pscan.control;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;

import com.ibm.pscan.dataHelper.DeleteFile;
import com.ibm.pscan.dataHelper.GetParticipant;
import com.ibm.pscan.dataHelper.PrepareForAzure;
import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.util.Config;
import com.ibm.pscan.util.IOPath;

/**
 * Download the file from yammer
 * Get the participant information from the input file
 * 
 * @author Ningxin
 */

public class FormatInputOne {
	
    //unzip file
	private static String fileName = IOPath.ZIP_FILE;
    private static String filePath = IOPath.UNZIP_FILE;

    //delete the downloaded file
    private static String deleteFileName=IOPath.ZIP_BASE;
	
	private static String containerName=Config.CONTAINER_NAME;
	
	private static String inputFilePath1=Config.BASE_PATH+"input/"+"userRelation.txt";
	private static String inputFilePath2=Config.BASE_PATH+"input/"+"findNeighbor/reply.txt";
	
	private static String outputFilePath1=Config.FORMAT_INPUT+"/"+"userRelation.txt";
	private static String outputFilePath2=Config.FORMAT_INPUT+"/"+"findNeighbor/reply.txt";
	
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		
		PrepareForAzure preparation=new PrepareForAzure(fileName,filePath);
		preparation.prepare();
		DeleteFile.deleteFolder(conf, deleteFileName); 
		
		File theDir = new File(Config.BASE_PATH+"input/"+"findNeighbor");
		theDir.mkdirs();

		GetParticipant.participant(Config.STORAGE_FILE_NAME, Config.BASE_PATH+"input", conf);	
		
		AzureIO.uploadToAzure(inputFilePath1,containerName,outputFilePath1);
		AzureIO.uploadToAzure(inputFilePath2,containerName,outputFilePath2);
		
		//delete the unused file
		File theDirDelete = new File(Config.BASE_PATH+"input");
		FileUtils.deleteDirectory(theDirDelete);
		
		System.out.println("finish...");
		
	}
}
