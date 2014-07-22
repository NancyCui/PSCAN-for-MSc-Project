import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableComparable;

/*
 * Represent edges in a network
 * Represent a flight route between two cities
 * book-page 47
 */
public class Edge implements WritableComparable<Edge>{
	
	private String departureNode;
	private String arrivalNode;
	
	public String getDepartureNode(){
		return departureNode;
	}
	
	/*
	 * Specify how to read data in
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		departureNode = in.readUTF();
		arrivalNode = in.readUTF();
	}
	
	/*
	 * Specify how to write data out
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(departureNode);
		out.writeUTF(arrivalNode);		
	}

	/*
	 * Define ordering of data
	 * It returns -1, 0, 1, if the called Edge is 
	 * less than, equal to, or greater than the given Edges
	 */
	@Override
	public int compareTo(Edge o) {		
		return (departureNode.compareTo(o.departureNode)!=0)
				?departureNode.compareTo(o.departureNode)
				:arrivalNode.compareTo(o.arrivalNode);
	}

}
