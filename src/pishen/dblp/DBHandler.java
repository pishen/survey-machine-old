package pishen.dblp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.ReadableIndex;

public class DBHandler {
	
	private GraphDatabaseService graphDB;
	private ReadableIndex<Node> autoNodeIndex;
	
	public void startGraphDB(){
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("graph-db")
				.setConfig(GraphDatabaseSettings.node_keys_indexable, Record.getConcatenatedKey())
				.setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
				.newGraphDatabase();
		autoNodeIndex = graphDB.index().getNodeAutoIndexer().getAutoIndex();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				graphDB.shutdown();
			}
		});
	}
	
	public void setPropertyOfNode(Node node, String key, Object value){
		Transaction tx = graphDB.beginTx();
		try {
			node.setProperty(key, value);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public Node getNodeWithRecordKey(String recordKey){
		Node node = autoNodeIndex.get(Record.KEY, recordKey).getSingle();
		if(node != null){
			return node;
		}else{
			createNodeWithRecordKey(recordKey);
			return autoNodeIndex.get(Record.KEY, recordKey).getSingle();
		}
	}
	
	private void createNodeWithRecordKey(String recordKey){
		Transaction tx = graphDB.beginTx();
		try {
			Node node = graphDB.createNode();
			node.setProperty(Record.KEY, recordKey);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	
	
}
