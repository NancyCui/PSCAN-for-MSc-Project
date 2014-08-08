import io.ArrayListWritable;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import fileIO.SequenceFileIO;


public class findInputPara {
	
	public ArrayListWritable<ArrayListWritable<Text>> readVertexSimi() throws IOException{
		String bathPath = "/Users/Nancy/Documents/Java/PSCAN/";
		String fileSimi=bathPath+"output0-InputPara/part-r-00000";
		
	    Configuration conf = new Configuration();
	    FileSystem fsSimi = FileSystem.get(URI.create(fileSimi), conf);
	    Path pathSimi = new Path(fileSimi);
	    
	    ArrayListWritable<ArrayListWritable<Text>> simi=SequenceFileIO.readSequenceFileTD(fsSimi, pathSimi, conf);
		
	    return simi; 	    
	}
	
}
