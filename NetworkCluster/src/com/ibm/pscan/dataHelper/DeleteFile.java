package com.ibm.pscan.dataHelper;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DeleteFile {
	
	/**
	 * Delete specific folder
	 */
	public static void deleteFolder(Configuration conf, String fileName) throws IOException{
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(fileName),true);
	}
	
}
