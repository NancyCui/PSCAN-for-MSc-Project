import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableComparable;

/*
 * Represent edges in a network
 * Represent the edge connecting the input vertex and its neighbor
 * book-page 47
 */
public class Edge implements WritableComparable<Edge>{
	
	private String inputVertex;
	private String neighbor;
	
	public String getInputVertex(){
		return inputVertex;
	}
	
	/*
	 * Specify how to read data in
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		inputVertex = in.readUTF();
		neighbor = in.readUTF();
	}
	
	/*
	 * Specify how to write data out
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(inputVertex);
		out.writeUTF(neighbor);		
	}

	/*
	 * Define ordering of data
	 * It returns -1, 0, 1, if the called Edge is 
	 * less than, equal to, or greater than the given Edges
	 */
	@Override
	public int compareTo(Edge o) {		
		return (inputVertex.compareTo(o.inputVertex)!=0)
				?inputVertex.compareTo(o.inputVertex)
				:neighbor.compareTo(o.neighbor);
	}

}
