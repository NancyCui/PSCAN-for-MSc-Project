package com.ibm.pscan.control;

import org.apache.hadoop.conf.Configuration;

import com.ibm.pscan.dataHelper.FindInputPara;
import com.ibm.pscan.mapreduce.AdListMapReduce;
import com.ibm.pscan.mapreduce.InputParaMapReduce;
import com.ibm.pscan.mapreduce.PCSSMapReduce;
import com.ibm.pscan.util.IOPath;

public class InputPara {

	private static double thresHold=0.0;
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		//get the adjacency list, which will be the input of PCSS
		//input: conf, inputFile, outputFile
		AdListMapReduce adListMapReduce=AdListMapReduce.getInstance();
		adListMapReduce.adList(conf, IOPath.ADLIST_INPUT_0, IOPath.ADLIST_OUTPUT_0);
		
		//calculate the structural similarity
		PCSSMapReduce pcssMapReduce=PCSSMapReduce.getInstance(thresHold);
		pcssMapReduce.PCSS(conf,IOPath.PCSS_INPUT_0, IOPath.PCSS_OUTPUT_0);
		
		//Get the largest similarity for each node
		InputParaMapReduce inputParaMapReduce=InputParaMapReduce.getInstance();
		inputParaMapReduce.getSimiForFindInput(conf,IOPath.INPUTPARA_INPUT, IOPath.INPUTPARA_OUTPUT);
		
		FindInputPara.findInputPara(IOPath.FINDINPUTPARA_INPUT);
		

	}

}
