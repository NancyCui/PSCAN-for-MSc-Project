import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

/*
 * Want all edges with a common departure point to be sent to the same reducer.
 * Hashing the departureNode member of the Edge
 */
public class EdgePartitioner implements Partitioner<Edge, Writable> {

	@Override
	public void configure(JobConf conf) { }

	@Override
	public int getPartition(Edge key, Writable value, int numPartitions) {		
		return key.getDepartureNode().hashCode() % numPartitions;
	}

}
