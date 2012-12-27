package pishen.dblp;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;

public class DBHandler {
	private static final Logger log = Logger.getLogger(DBHandler.class);
	private static final String RECORD_KEY = "RECORD_KEY";
	private GraphDatabaseService graphDB;
	private ReadableIndex<Node> autoNodeIndex;
	
	public void startGraphDB(){
		log.info("starting graph DB...");
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("graph-db")
				.setConfig(GraphDatabaseSettings.node_keys_indexable, createConcatenatedKey())
				.setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
				.newGraphDatabase();
		//link Record with graphDB for Record to create Transaction by graphDB
		Record.setGraphDB(graphDB);
		
		//auto-indexing all the keys in record except RECORD_KEY
		autoNodeIndex = graphDB.index().getNodeAutoIndexer().getAutoIndex();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				graphDB.shutdown();
			}
		});
	}
	
	public Record getRecordWithKey(String recordKeyValue){
		Node node = autoNodeIndex.get(RECORD_KEY, recordKeyValue).getSingle();
		if(node == null){
			node = createNodeWithRecordKey(recordKeyValue);
		}
		return new Record(node);
	}
	
	public List<Record> getRecords(Key key, Object value){
		IndexHits<Node> hits = autoNodeIndex.get(key.toString(), value);
		List<Record> list = new ArrayList<Record>();
		try {
			for(Node node: hits){
				list.add(new Record(node));
			}
			return list;
		} finally {
			hits.close();
		}
	}
	
	private Node createNodeWithRecordKey(String recordKeyValue){
		log.debug("creating new Node");
		Transaction tx = graphDB.beginTx();
		try {
			Node node = graphDB.createNode();
			node.setProperty(RECORD_KEY, recordKeyValue);
			tx.success();
			return node;
		} finally {
			tx.finish();
		}
	}
	
	private String createConcatenatedKey(){
		String concatKey = RECORD_KEY;
		for(Key k: Key.values()){
			concatKey = concatKey + "," + k;
		}
		return concatKey;
	}
	
}
