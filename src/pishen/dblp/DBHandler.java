package pishen.dblp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.ReadableIndex;

public class DBHandler {
	private static final String RECORD_KEY = "record_key";
	private GraphDatabaseService graphDB;
	private ReadableIndex<Node> autoNodeIndex;
	private Index<Node> keyNodeIndex;
	
	public void startGraphDB(){
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("graph-db")
				.setConfig(GraphDatabaseSettings.node_keys_indexable, RECORD_KEY)
				.setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
				.newGraphDatabase();
		autoNodeIndex = graphDB.index().getNodeAutoIndexer().getAutoIndex();
		keyNodeIndex = graphDB.index().forNodes(RECORD_KEY);
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				graphDB.shutdown();
			}
		});
	}
	
	public void printTitleWithKey(String key){
		Node node = keyNodeIndex.get(RECORD_KEY, key).getSingle();
		Transaction tx = graphDB.beginTx();
		try {
			node.setProperty(RECORD_KEY, key);
			tx.success();
		} finally {
			tx.finish();
		}
		node = autoNodeIndex.get(RECORD_KEY, key).getSingle();
		System.out.println("title=" + node.getProperty("title"));
	}
	
	public void createNodeWithKey(String key, String title){
		Transaction tx = graphDB.beginTx();
		try {
			Node node = graphDB.createNode();
			node.setProperty(RECORD_KEY, key);
			node.setProperty("title", title);
			keyNodeIndex.add(node, RECORD_KEY, key);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	
	
}
