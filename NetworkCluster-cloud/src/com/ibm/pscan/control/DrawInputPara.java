package com.ibm.pscan.control;

import java.util.ArrayList;
import java.util.List;

import com.ibm.pscan.gui.LineChartController;
import com.ibm.pscan.io.AzureIO;
import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.util.Config;

/**
 * Download finalSimi.csv from cloud and draw the graph
 * 
 * @author Ningxin
 */
public class DrawInputPara {
	
	private static String cloudFilePath="example/data/pscanOutput/finalSimi.csv";
	private static String outputFilePath=Config.BASE_PATH+"finalSimi.csv";
	private static String containerName=Config.CONTAINER_NAME;
	
	public static void main(String[] args) throws Exception {
		AzureIO.downloadFromAzure(cloudFilePath,containerName, outputFilePath);
		ArrayList<ArrayList<String>> contents=CsvFileIO.readCSVFile(outputFilePath);
		System.out.println(contents.get(0));
		List<Double> finalSimi=new ArrayList<Double>();
		for(int i=0;i<contents.get(0).size();i++){
			double simi=Double.parseDouble(contents.get(0).get(i));
			finalSimi.add(simi);
		}
		LineChartController.drawLineChart(finalSimi);
	}
}
