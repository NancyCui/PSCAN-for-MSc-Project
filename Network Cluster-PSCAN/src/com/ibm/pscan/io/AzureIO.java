package com.ibm.pscan.io;

import java.io.*;

import com.ibm.pscan.util.Config;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

/**
 * Upload the file to Azure
 * 
 * @author Ningxin
 */

public class AzureIO {

	public static void uploadToAzure(String inputFilePath, String contianerName, String fileName){
		try {
			CloudStorageAccount account = CloudStorageAccount.parse(Config.storageConnectionString);
	        CloudBlobClient serviceClient = account.createCloudBlobClient();
	        
	        // Container name must be lower case.
	        CloudBlobContainer container = serviceClient.getContainerReference(contianerName);		            
	        container.createIfNotExists();
	           
	        // Upload an file.
	        CloudBlockBlob blob = container.getBlockBlobReference(fileName);
	        File sourceFile = new File(inputFilePath);
	        blob.upload(new FileInputStream(sourceFile), sourceFile.length());	 
	         
	        System.out.println("Finish uploading the source file to Azure.");
		}
		catch (FileNotFoundException fileNotFoundException) {
			 System.out.print("FileNotFoundException encountered: ");
			 System.out.println(fileNotFoundException.getMessage());
			 System.exit(-1);
		}
		catch (StorageException storageException) {
			System.out.print("StorageException encountered: ");
			System.out.println(storageException.getMessage());
			System.exit(-1);
		}
		catch (Exception e) {
			System.out.print("Exception encountered: ");
			System.out.println(e.getMessage());
			System.exit(-1);
		}	
		
	}
	
	public static void downloadFromAzure(String inputFilePath, String contianerName, String fileName){
		try {
			CloudStorageAccount account = CloudStorageAccount.parse(Config.storageConnectionString);
	        CloudBlobClient serviceClient = account.createCloudBlobClient();
	        
	        // Container name must be lower case.
	        CloudBlobContainer container = serviceClient.getContainerReference(contianerName);		            
	        container.createIfNotExists();

	        // Download an file.
	        CloudBlockBlob blob = container.getBlockBlobReference(inputFilePath);
	        blob.download(new FileOutputStream(fileName));	 
	         
	        System.out.println("Finish download the file from Azure.");

	        

		}
		catch (FileNotFoundException fileNotFoundException) {
			 System.out.print("FileNotFoundException encountered: ");
			 System.out.println(fileNotFoundException.getMessage());
			 System.exit(-1);
		}
		catch (StorageException storageException) {
			System.out.print("StorageException encountered: ");
			System.out.println(storageException.getMessage());
			System.exit(-1);
		}
		catch (Exception e) {
			System.out.print("Exception encountered: ");
			System.out.println(e.getMessage());
			System.exit(-1);
		}	
		
	}
	 
}
