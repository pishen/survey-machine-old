package pishen.dblp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;


public class Record {
	private static GraphDatabaseService graphDB;
	private Node node;
	
	public static void setGraphDB(GraphDatabaseService graphDB){
		Record.graphDB = graphDB;
	}
	
	public Record(Node node){
		this.node = node;
	}
	
	public void setProperty(Key key, Object value){
		Transaction tx = graphDB.beginTx();
		try {
			node.setProperty(key.toString(), value);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public Object getProperty(Key key){
		return node.getProperty(key.toString(), null);
	}
	
}
