package pishen.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;
import org.neo4j.tooling.GlobalGraphOperations;

import pishen.core.Key;

public class DBHandler {
	private static final Logger log = Logger.getLogger(DBHandler.class);
	private static final String CONCAT_KEY = createConcatenatedKey();
	private static GraphDatabaseService graphDB;
	private static ReadableIndex<Node> autoNodeIndex;
	
	public static void startGraphDB(){
		log.info("starting graph DB...");
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("graph-db")
				.setConfig(GraphDatabaseSettings.node_keys_indexable, CONCAT_KEY)
				.setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
				.newGraphDatabase();
		//link Record with graphDB for Record to create Transaction by graphDB
		DBRecord.setGraphDB(graphDB);
		
		deleteReferenceNode();
		
		//auto-indexing all the keys in record except RECORD_KEY
		autoNodeIndex = graphDB.index().getNodeAutoIndexer().getAutoIndex();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				log.info("shutting down graphDB");
				graphDB.shutdown();
			}
		});
	}
	
	public static DBRecordIterator iteratorForRecord(){
		return new DBRecordIterator(GlobalGraphOperations.at(graphDB).getAllNodes().iterator());
	}
	
	public static DBRecord getRecordWithKey(String recordKeyValue){
		Node node = autoNodeIndex.get(DBRecord.RECORD_KEY, recordKeyValue).getSingle();
		if(node == null){
			node = createNodeWithRecordKey(recordKeyValue);
		}
		return new DBRecord(node);
	}
	
	public static List<DBRecord> getRecords(Key key, Object value){
		IndexHits<Node> hits = autoNodeIndex.get(key.toString(), value);
		List<DBRecord> list = new ArrayList<DBRecord>();
		try {
			for(Node node: hits){
				list.add(new DBRecord(node));
			}
			return list;
		} finally {
			hits.close();
		}
	}
	
	private static Node createNodeWithRecordKey(String recordKeyValue){
		log.debug("creating new Node");
		Transaction tx = graphDB.beginTx();
		try {
			Node node = graphDB.createNode();
			node.setProperty(DBRecord.RECORD_KEY, recordKeyValue);
			tx.success();
			return node;
		} finally {
			tx.finish();
		}
	}
	
	private static String createConcatenatedKey(){
		String concatKey = DBRecord.RECORD_KEY;
		for(Key k: Key.values()){
			concatKey = concatKey + "," + k;
		}
		return concatKey;
	}
	
	private static void deleteReferenceNode(){
		Transaction tx = graphDB.beginTx();
		try {
			graphDB.getReferenceNode().delete();
			tx.success();
		} catch(NotFoundException e) {
			log.info("Reference Node already deleted.");
		} finally {
			tx.finish();
		}
	}
	
}
