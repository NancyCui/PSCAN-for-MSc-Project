package com.ibm.pscan.control;

import java.util.ArrayList;
import java.util.List;

import com.ibm.pscan.gui.LineChartController;
import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.util.Config;

/**
 * Download finalSimi.csv from cloud and draw the graph
 * 
 * @author Ningxin
 */
public class DrawInputPara {
	
	private static String path="example/data/pscanOutput/finalSimi.csv";
	private static String filePath="/Users/Nancy/Desktop";
	private static String containerName=Config.CONTAINER_NAME;
	
	public static void main(String[] args) throws Exception {
		AzureIO.downloadFromAzure(path,containerName, filePath);
//		List<Double> finalSimi=new ArrayList<Double>();
//		LineChartController.drawLineChart(finalSimi);
	}
}
