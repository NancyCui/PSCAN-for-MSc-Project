package fileIO;

import io.ArrayListWritable;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;

public class SequenceFileReader {
	
	/**
	 * Read sequence file and store the key value pair into ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>>
	 * @param fs
	 * @param path
	 * @param conf
	 * @return 
	 * @throws IOException
	 */
	@SuppressWarnings("finally")
	public static ArrayListWritable<ArrayListWritable<ArrayListWritable<Text>>> readSequenceFile(FileSystem fs, Path path,
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
	
		



}
