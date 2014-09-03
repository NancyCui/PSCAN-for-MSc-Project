package com.ibm.pscan.control;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import com.ibm.pscan.dataHelper.DeleteFile;
import com.ibm.pscan.dataHelper.PrepareForAzure;
import com.ibm.pscan.util.Config;
import com.ibm.pscan.util.IOPath;

/**
 * Download data from yammer and upload to the azure storage for further process
 * 
 * @author Ningxin
 */

public class YammerToAzure {
    //unzip file
	private static String fileName = IOPath.ZIP_FILE;
    private static String filePath = IOPath.UNZIP_FILE;
    //upload to azure
    private static String inputFilePath = IOPath.INPUT_FILE_PATH;
    private static String containerName=Config.CONTAINER_NAME;
    private static String storageFileName=Config.STORAGE_FILE_NAME;
    //delete the downloaded file
    private static String deleteFileName=IOPath.ZIP_BASE;
	
	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		
		PrepareForAzure preparation=new PrepareForAzure(fileName,filePath, inputFilePath,containerName,storageFileName);
		preparation.prepare();
		DeleteFile.deleteFolder(conf, deleteFileName); 

    }

}
