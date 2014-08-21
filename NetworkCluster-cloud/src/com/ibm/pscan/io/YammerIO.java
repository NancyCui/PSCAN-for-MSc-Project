package com.ibm.pscan.io;

import static org.apache.commons.io.FileUtils.copyURLToFile;

import java.io.File;
import java.net.URL;

import com.ibm.pscan.util.Config;
import com.ibm.pscan.util.IOPath;

/**
 * Download data from yammer
 * 
 * @author Ningxin
 */

public class YammerIO {
	
	 private static URL dl = null;
     private static File fl = null;
     
     public static void downloadMessage(){
         try {
        	 fl = new File(IOPath.ZIP_FILE);
        	 //RESTful get from yammer
             dl = new URL(Config.YAMMER_PATH);
             copyURLToFile(dl, fl);
             System.out.println("download successful, starting unzip and upload to yammer...");
         } catch (Exception e) {
             System.out.println(e);
         }
     }    

}

