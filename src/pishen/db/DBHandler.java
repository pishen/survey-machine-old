package pishen.db;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;

import pishen.core.RecordKey;

public class DBHandler {
	public static final String NODE_TYPE = "TYPE";
	
	private static final Logger log = Logger.getLogger(DBHandler.class);
	private static final String CONCAT_KEY = createConcatenatedKey();
	private static GraphDatabaseService graphDB;
	private static ReadableIndex<Node> autoNodeIndex;
	
	public static void startGraphDB(){
		log.info("starting graph DB");
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("graph-db")
				.setConfig(GraphDatabaseSettings.node_keys_indexable, CONCAT_KEY)
				.setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
				.newGraphDatabase();
		//link Record with graphDB for Record to create Transaction by graphDB
		DBRecord.setGraphDB(graphDB);
		
		autoNodeIndex = graphDB.index().getNodeAutoIndexer().getAutoIndex();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				log.info("shutting down graphDB");
				graphDB.shutdown();
			}
		});
	}
	
	public static RecordHits getAllRecords(){
		IndexHits<Node> indexHits = autoNodeIndex.get(NODE_TYPE, NodeType.RECORD);
		return new RecordHits(indexHits);
	}
	
	public static RecordHits getRecords(RecordKey key, Object value){
		IndexHits<Node> hits = autoNodeIndex.get(key.toString(), value);
		return new RecordHits(hits);
	}
	
	public static DBRecord getOrCreateRecord(String recordName){
		Transaction tx = graphDB.beginTx();
		try {
			Node node = autoNodeIndex.get(DBRecord.NAME, recordName).getSingle();
			if(node == null){
				node = graphDB.createNode();
				node.setProperty(DBRecord.NAME, recordName);
			}
			tx.success();
			return new DBRecord(node);
		} finally {
			tx.finish();
		}
	}
	
	private static String createConcatenatedKey(){
		String concatKey = NODE_TYPE + "," + DBRecord.NAME;
		for(RecordKey k: RecordKey.values()){
			concatKey = concatKey + "," + k;
		}
		return concatKey;
	}
	
}
