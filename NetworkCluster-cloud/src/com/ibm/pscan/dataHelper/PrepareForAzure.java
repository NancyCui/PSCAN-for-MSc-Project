package com.ibm.pscan.dataHelper;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.ibm.pscan.io.YammerIO;
import com.ibm.pscan.util.Config;

public class PrepareForAzure {
	//unzip file
	private String fileName;
	private String filePath;

	public PrepareForAzure(String fileName, String filePath){
		this.fileName=fileName;
		this.filePath=filePath;
	}
	
	public void prepare(){
		//download the data from yammer
		YammerIO.downloadMessage(); 
		//unzip the downloaded file
		UnZip.unZipFile(fileName, filePath);
		
		File source = new File(filePath+"/Messages.csv");
		File dest = new File(Config.STORAGE_FILE);
		File source2 = new File(filePath+"/Users.csv");
		File dest2 = new File(Config.STORAGE_FILE);
		try {
		    FileUtils.copyFileToDirectory(source, dest);
		    FileUtils.copyFileToDirectory(source2, dest2);
		    System.out.println("Finishied");
		} catch (IOException e) {
		    e.printStackTrace();
		};
	}
}
