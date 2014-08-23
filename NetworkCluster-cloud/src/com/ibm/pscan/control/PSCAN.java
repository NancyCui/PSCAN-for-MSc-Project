package com.ibm.pscan.control;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;

import com.ibm.pscan.dataHelper.FindInputPara;
import com.ibm.pscan.io.CsvFileIO;
import com.ibm.pscan.mapreduce.ClusterMapReduce;
import com.ibm.pscan.mapreduce.HubsFinderMapReduce;
import com.ibm.pscan.mapreduce.InputParaMapReduce;
import com.ibm.pscan.mapreduce.LPCCAfterMapReduce;
import com.ibm.pscan.mapreduce.LPCCMapReduce;
import com.ibm.pscan.mapreduce.PCSSMapReduce;
import com.ibm.pscan.mapreduce.PCSStoLPCCMapReduce;
import com.ibm.pscan.mapreduce.PSCANMapReduce;

public class PSCAN {
	
	public static final String ARGS_PATH="wasb:///example/data/pscanOutput/args.csv";
	
	public static void main(String[] args) throws Exception {
		
		//Get the input and output path of PSCAN
		Configuration conf = new Configuration();
		
		FileSystem fs = FileSystem.get(conf);
		
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		
	    if (otherArgs.length != 3) {
	      System.err.println("Usage: PSCAN <in> <out> <threshold>");
	      System.exit(2);
	    }
	    
	    //if the outputFile exists, delete the outputfile
	    if(fs.exists(new Path(otherArgs[1]))){
	    	fs.delete(new Path(otherArgs[1]), true);
	    }
	    
	    //delete the log file
	    if(fs.exists(new Path("wasb:///app-logs/admin/logs"))){
	    	fs.delete(new Path("wasb:///app-logs/admin/logs"), true);
	    }

	    //write the input parameters into to a csv file on cloud
	    writeArguments(otherArgs, conf);
	    
	    double thresHold=Double.parseDouble(otherArgs[2]);
	    if(thresHold>0){
	    	calculatePSCAN(conf, otherArgs);
	    }
	    else{
	    	getInputPara(conf,otherArgs);
	    }
		
		fs.close();
		
	}

	/**
	 * Write the threhold into a file on cloud
	 */
	private static void writeArguments(String[] otherArgs, Configuration conf) {
		 CsvFileIO.writeArrayToCSV(ARGS_PATH, otherArgs, conf);
		 System.out.println("Write input parameters successful.");
	
	}

	/**
	 * The Map Reduce progress for calculate the threshold of the networks
	 */
	private static void getInputPara(Configuration conf, String[] otherArgs) throws ClassNotFoundException, IOException, InterruptedException, Exception {
		if(PSCANMapReduce.findNeighbor(conf,otherArgs[0], otherArgs[1])){
			//delete duplicate records
			if(PSCANMapReduce.deleteDuplication(conf,otherArgs[1])){
				//get the adjustList of each node
				if(PSCANMapReduce.getAdjacencyList(conf,otherArgs[1])){
					//calculate the structural similarity
					if(PCSSMapReduce.doPCSS(conf,otherArgs[1])){
						if(InputParaMapReduce.findInputPara(conf,otherArgs[1])){
							FindInputPara.findInputPara(otherArgs[1], conf);
						}
					}
				
				}
			}
		}
		
	}

	/**
	 * Cluster the nodes into groups and find hubs and outliners
	 */
	private static void calculatePSCAN(Configuration conf, String[] otherArgs) throws ClassNotFoundException, IOException, InterruptedException, Exception {
		//delete the duplicate records in the original file and get the neighbor relation
		if(PSCANMapReduce.findNeighbor(conf,otherArgs[0], otherArgs[1])){
			//delete duplicate records
			if(PSCANMapReduce.deleteDuplication(conf,otherArgs[1])){
				//get the adjustList of each node
				if(PSCANMapReduce.getAdjacencyList(conf,otherArgs[1])){
					//calculate the structural similarity
					if(PCSSMapReduce.doPCSS(conf,otherArgs[1])){
						//get the new adjacency list of the cutted graph
						if(PCSStoLPCCMapReduce.getAdList(conf,otherArgs[1])){
							//do LPCC
							LPCCMapReduce.LPCC(conf, otherArgs[1]);
							//Find Hubs and Outliners
							LPCCAfterMapReduce.findNonMember(conf,otherArgs[1]);
								//format the output of LPCC
								if(ClusterMapReduce.convert(conf, otherArgs[1])){
									//get each clusterID and clusterMembers
									HubsFinderMapReduce.findHubs(conf, otherArgs[1]);
								}
							
						}
					}
				}
			}
		}
		
	}
}
