package com.ibm.pscan.dataHelper;
import com.ibm.pscan.type.ArrayListWritable;
import com.ibm.pscan.util.IOPath;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.io.SequenceFileIO;

/**
 * Read the sequence file and write the least 10% sturctural similarity into a csv file
 * Call method "findInputPara(String inputFile)"
 * 
 * @author Ningxin
 */

public class FindInputPara {
	
	/**
	 * Read the file and get the similarity for each node
	 * 
	 * @param inputFile
	 * @throws IOException
	 */
	private static void readVertexSimi(String inputFile) throws IOException{
		
	    Configuration conf = new Configuration();
	    FileSystem fsSimi = FileSystem.get(URI.create(inputFile), conf);
	    Path pathSimi = new Path(inputFile);
	    
	    ArrayListWritable<ArrayListWritable<Text>> simi=SequenceFileIO.readSequenceFileTD(fsSimi, pathSimi, conf);
		
	    System.out.println(simi);
	    
	    getSimi(simi);
	    
	}
	
	/**
	 * Get the similarity and rank them in ASC order
	 * Store the first 10% similarity into an ArrayList<Double>
	 * 
	 * @param simi
	 */
	private static void getSimi(ArrayListWritable<ArrayListWritable<Text>> simi) {
		ArrayList<Double> similarity= new ArrayList<Double>();
		for(ArrayListWritable<Text> s: simi){
			similarity.add(Double.parseDouble(s.get(1).toString()));
		}
		Collections.sort(similarity);
		
		int simiLength=similarity.size();
		//Get the length of how many sample data need to be take and how many value need to treat as one unit
		ArrayList<Integer> length_unitLength=findLength(simiLength);
		int length=length_unitLength.get(0);
		int unit_Length=length_unitLength.get(1);

		List<Double> simiSublist=similarity.subList(0, length);
		
		//If unit-length > 1, calculate the average similarity value for each unit
		List<Double> finalSimi=new ArrayList<Double>();
		if(unit_Length>1){
			finalSimi= averageSimi(simiSublist, unit_Length);
		}
		else{
			finalSimi=simiSublist;
		}
	
		CsvFileIO.writeFile(IOPath.FINDINPUTPARA_OUTPUT, finalSimi);
		
	}

	/**
	 * Calculate the average similarity for each unit
	 * 
	 * @param simiSublist
	 * @param unit_Length 
	 */
	private static List<Double> averageSimi(List<Double> simiSublist, int unit_Length) {
		List<Double> finalSimi=new ArrayList<Double>();
		
		return finalSimi;
	}

	/**
	 * Calculate how many value should be took to judge the input parameter
	 * 
	 * @param simiLength
	 */
	private static ArrayList<Integer> findLength(int simiLength) {
		ArrayList<Integer> length_unitLength=new ArrayList<Integer>();
		//the length of the array
		int length=0;
		int unitLength=0;
		
		double floor=Math.floor(simiLength/10);
		if(floor>=100.0){
			//when the dataset is larger than 1000 nodes, take around 10% of the node as the sample
			int sampleLength=(int)(simiLength*0.1);
			double sampleFloor=Math.floor(sampleLength/10);
			unitLength=(int)(sampleFloor);
			length=(int)(sampleFloor*10);			
		}
		else if(floor>1.0&&floor<100.0){
			//when the dataset has 10-999 nodes
			unitLength=(int)(floor);
			length=(int)(floor*10);
		}
		else{
			unitLength=1;
			length=simiLength;
		}
		
		length_unitLength.add(length);
		length_unitLength.add(unitLength);
		
		return length_unitLength;
	}

	
	public static void findInputPara(String inputFile) throws Exception {
		 readVertexSimi(inputFile);
		
	}
	
}
