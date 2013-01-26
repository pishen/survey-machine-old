package pishen.dblp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;


public class DBRecord {
	private static GraphDatabaseService graphDB;
	private Node node;
	
	public static void setGraphDB(GraphDatabaseService graphDB){
		DBRecord.graphDB = graphDB;
	}
	
	public DBRecord(Node node){
		this.node = node;
	}
	
	public void setProperty(Key key, String value){
		if(value != null){
			Transaction tx = graphDB.beginTx();
			try {
				node.setProperty(key.toString(), value);
				tx.success();
			} finally {
				tx.finish();
			}
		}
	}
	
	public String getStringProperty(Key key){
		return (String)getProperty(key);
	}
	
	public Object getProperty(Key key){
		return node.getProperty(key.toString());
	}
	
}
