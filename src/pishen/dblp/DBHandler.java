package pishen.dblp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

public class DBHandler {
	private static final String RECORD_KEY = "record_key";
	private GraphDatabaseService graphDB;
	private Index<Node> nodeRecordIndex;
	
	public void startGraphDB(){
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase("graph-db");
		nodeRecordIndex = graphDB.index().forNodes(RECORD_KEY);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				graphDB.shutdown();
			}
		});
	}
	
	public void addNode(String key, String title){
		Transaction tx = graphDB.beginTx();
		try {
			Node node = graphDB.createNode();
			node.setProperty(RECORD_KEY, key);
			nodeRecordIndex.add(node, RECORD_KEY, key);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
}
