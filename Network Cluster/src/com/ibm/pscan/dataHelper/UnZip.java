package com.ibm.pscan.dataHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Unzip the file to the selected file path
 * 
 * @author Ningxin
 */

public class UnZip {
    private static int BUFFER = 2048;

    public static void unZipFile(String fileName, String filePath) {
        try {
        	ZipFile zipFile = new ZipFile(fileName);
            
            @SuppressWarnings("rawtypes")
			Enumeration emu = zipFile.entries();
            while(emu.hasMoreElements()){
                ZipEntry entry = (ZipEntry)emu.nextElement();
                
                if (entry.isDirectory())
                {
                    new File(filePath + entry.getName()).mkdirs();
                    continue;
                }
                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                File file = new File(filePath + entry.getName());
                
                File parent = file.getParentFile();
                if(parent != null && (!parent.exists())){
                    parent.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos,BUFFER);           
                
                int count;
                byte data[] = new byte[BUFFER];
                while ((count = bis.read(data, 0, BUFFER)) != -1)
                {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();
                bis.close();                
            }
            zipFile.close();
            System.out.println("Unzip Successful, try to upload to Azure");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
