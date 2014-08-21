package com.ibm.pscan.io;

import com.ibm.pscan.type.ArrayListWritable;
import com.ibm.pscan.type.Edge;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * Read Sequence File of different types of key value pair
 * 
 * @param fs path conf
 * @return output
 * @throws IOException
 * 
 * @author Ningxin
 */

public class SequenceFileIO {
	
	/**
	 * Read sequence file and store the key value pair into ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>>
	 * Key: Text
	 * Value: ArrayListWritable<ArrayListWritable<Text>>
	 */
	@SuppressWarnings("finally")
	public static ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> readSequenceFileTAA(FileSystem fs, Path path,
			Configuration conf) throws IOException {

		ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> output=new ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>>();
		
		SequenceFile.Reader reader = null;
	    try {
	      reader = new SequenceFile.Reader(fs, path, conf);
	      Text key = (Text)ReflectionUtils.newInstance(reader.getKeyClass(), conf);
	      @SuppressWarnings("unchecked")
	      ArrayListWritable<ArrayListWritable<Text>> value =  (ArrayListWritable<ArrayListWritable<Text>>)ReflectionUtils.newInstance(reader.getValueClass(), conf);
	      while (reader.next(key, value)) {
	    	  ArrayListWritable<ArrayListWritable<Text>> keyValue=new ArrayListWritable<ArrayListWritable<Text>>();
	    	  ArrayListWritable<Text> newKey=new ArrayListWritable<Text>();
	    	  newKey.add(new Text(key.toString()));
	    	  keyValue.add(newKey);
	    	  for(int i=0;i<value.size();i++){
	    		  keyValue.add(new ArrayListWritable<Text>(value.get(i)));
	    	  }	
	    	  output.add(keyValue);	    	  
	      }
	    } finally {
	      IOUtils.closeStream(reader);
	      return output;
	    }		
	}
	
	/**
	 * Read sequence file and store the key value pair into ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>>
	 * Key: Text
	 * Value: ArrayListWritable<Text>
	 */
	@SuppressWarnings("finally")
	public static ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> readSequenceFileTA(FileSystem fs, Path path,
			Configuration conf) throws IOException {

		ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> output=new ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>>();
		
		SequenceFile.Reader reader = null;
	    try {
	      reader = new SequenceFile.Reader(fs, path, conf);
	      Text key = (Text)ReflectionUtils.newInstance(reader.getKeyClass(), conf);
	      @SuppressWarnings("unchecked")
	     ArrayListWritable<Text> value =  (ArrayListWritable<Text>)ReflectionUtils.newInstance(reader.getValueClass(), conf);
	      while (reader.next(key, value)) {
	    	  ArrayListWritable<ArrayListWritable<Text>> keyValue=new ArrayListWritable<ArrayListWritable<Text>>();
	    	  ArrayListWritable<Text> newKey=new ArrayListWritable<Text>();
	    	  newKey.add(new Text(key.toString()));
	    	  keyValue.add(newKey);	    	  
	    	  keyValue.add(new ArrayListWritable<Text>(value));	    	  	
	    	  output.add(keyValue);	    	  
	      }
	    } finally {
	      IOUtils.closeStream(reader);
	      return output;
	    }		
	}
	
	/**
	 * Read sequence file and store the key value pair into ArrayListWritable<ArrayListWritable<Text>>
	 * Key: Edge
	 * Value: DoubleWritable
	 */
	@SuppressWarnings("finally")
	public static ArrayListWritable<ArrayListWritable<Text>> readSequenceFileED(FileSystem fs, Path path,
			Configuration conf) throws IOException {

		ArrayListWritable<ArrayListWritable<Text>> output=new ArrayListWritable<ArrayListWritable<Text>>();
		
		SequenceFile.Reader reader = null;
	    try {
	      reader = new SequenceFile.Reader(fs, path, conf);
	      Edge key = (Edge)ReflectionUtils.newInstance(reader.getKeyClass(), conf);
	      DoubleWritable value =  (DoubleWritable)ReflectionUtils.newInstance(reader.getValueClass(), conf);
	      while (reader.next(key, value)) {
	    	  ArrayListWritable<Text> keyValue=new ArrayListWritable<Text>();
	    	  keyValue.add(new Text(key.getInputVertex()));
	    	  keyValue.add(new Text(key.getNeighbor()));	
	    	  keyValue.add(new Text(value.toString()));	    	  	
	    	  output.add(keyValue);	    	  
	      }
	    } finally {
	      IOUtils.closeStream(reader);
	      return output;
	    }		
	}
	
	/**
	 * Read sequence file and store the key value pair into ArrayListWritable<ArrayListWritable<Text>>
	 * Key: Text
	 * Value: DoubleWritable
	 */
	@SuppressWarnings("finally")
	public static ArrayListWritable<ArrayListWritable<Text>> readSequenceFileTD(FileSystem fs, Path path,
			Configuration conf) throws IOException {

		ArrayListWritable<ArrayListWritable<Text>> output=new ArrayListWritable<ArrayListWritable<Text>>();
		
		SequenceFile.Reader reader = null;
	    try {
	      reader = new SequenceFile.Reader(fs, path, conf);
	      Text key = (Text)ReflectionUtils.newInstance(reader.getKeyClass(), conf);
	      DoubleWritable value =  (DoubleWritable)ReflectionUtils.newInstance(reader.getValueClass(), conf);
	      while (reader.next(key, value)) {
	    	  ArrayListWritable<Text> keyValue=new ArrayListWritable<Text>();
	    	  keyValue.add(new Text(key.toString()));	
	    	  keyValue.add(new Text(value.toString()));	    	  	
	    	  output.add(keyValue);	    	  
	      }
	    } finally {
	      IOUtils.closeStream(reader);
	      return output;
	    }		
	}

}
