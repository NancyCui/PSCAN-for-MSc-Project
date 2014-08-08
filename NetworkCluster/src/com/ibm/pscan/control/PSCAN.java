package com.ibm.pscan.control;

import com.ibm.pscan.mapreduce.AdListMapReduce;
import com.ibm.pscan.mapreduce.LPCCMapReduce;
import com.ibm.pscan.mapreduce.PCSSMapReduce;
import com.ibm.pscan.mapreduce.PCSStoLPCCMapReduce;

public class PSCAN {
	
	private static double thresHold=0.7; //cut off the edges with the structural similarity less than threshold
	
	public static void main(String[] args) throws Exception {
		
		//get the adjacency list, which will be the input of PCSS
		AdListMapReduce adListMapReduce=AdListMapReduce.getInstance();
		adListMapReduce.adList();
		
		//calculate the structural similarity and cutting the edges whose similarity less than thresHold
		PCSSMapReduce pcssMapReduce=PCSSMapReduce.getInstance(thresHold);
		pcssMapReduce.PCSS();
		
		//change the output format of PCSS suitable for the input format of LPCC
		PCSStoLPCCMapReduce pcssToLPCCMapReduce=PCSStoLPCCMapReduce.getInstance();
		pcssToLPCCMapReduce.formateToLPCC();
		
		LPCCMapReduce.LPCC();
//		LPCCMapReduce lpccMapReduce=LPCCMapReduce.getInstance();
//		lpccMapReduce.LPCC();
		
		
		
	}
	
	
}
