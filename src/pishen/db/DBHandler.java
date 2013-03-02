package pishen.db;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;
import org.neo4j.tooling.GlobalGraphOperations;

import pishen.db.node.NodeType;
import pishen.db.node.Record;
import pishen.db.node.RecordHits;
import pishen.db.node.RecordKey;
import pishen.db.node.Reference;


public class DBHandler {
	private static final String NODE_TYPE = "TYPE";
	private static final Logger log = Logger.getLogger(DBHandler.class);
	private static final String CONCAT_KEY = createConcatenatedKey();
	private static GraphDatabaseService graphDB;
	private static ReadableIndex<Node> autoNodeIndex;
	private static RecordHits allRecordHits;
	private static int count;
	
	public static void startGraphDB(){
		log.info("starting graph DB");
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("graph-db")
				.setConfig(GraphDatabaseSettings.node_keys_indexable, CONCAT_KEY)
				.setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
				.newGraphDatabase();
		
		autoNodeIndex = graphDB.index().getNodeAutoIndexer().getAutoIndex();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				log.info("shutting down graphDB");
				graphDB.shutdown();
			}
		});
	}
	
	public static void initRecordIterator(){
		allRecordHits = getAllRecords();
		count = 0;
	}
	
	public static synchronized int count(){
		return ++count;
	}
	
	public static synchronized Record getNextRecord(){
		if(allRecordHits.hasNext()){
			return allRecordHits.next();
		}else{
			return null;
		}
	}
	
	//TODO clean
	public static void checkAll(){
		int recordCount = 0;
		int refCount = 0;
		int wrongCount = 0;
		int count = 0;
		for(Node node: GlobalGraphOperations.at(graphDB).getAllNodes()){
			log.info("[CHECK] #" + (++count));
			if(node.getProperty(NODE_TYPE).equals("RECORD")){
				recordCount++;
			}else if(node.getProperty(NODE_TYPE).equals("REFERENCE")){
				refCount++;
			}else{
				wrongCount++;
			}
		}
		log.info("record=" + recordCount + " ref=" + refCount + " wrong=" + wrongCount);
	}
	
	public static RecordHits getAllRecords(){
		IndexHits<Node> indexHits = autoNodeIndex.get(NODE_TYPE, NodeType.RECORD.toString());
		return new RecordHits(indexHits);
	}
	
	public static RecordHits getRecords(RecordKey key, Object value){
		IndexHits<Node> hits = autoNodeIndex.get(key.toString(), value);
		return new RecordHits(hits);
	}
	
	public static Record getOrCreateRecord(String recordName){
		Transaction tx = getTransaction();
		try {
			Node node = autoNodeIndex.get(Record.NAME, recordName).getSingle();
			if(node == null){
				node = graphDB.createNode();
				node.setProperty(NODE_TYPE, NodeType.RECORD.toString());
				node.setProperty(Record.NAME, recordName);
			}
			tx.success();
			return new Record(node);
		} finally {
			tx.finish();
		}
	}
	
	public static Reference createReference(){
		Transaction tx = graphDB.beginTx();
		try {
			Node node = graphDB.createNode();
			node.setProperty(NODE_TYPE, NodeType.REFERENCE.toString());
			tx.success();
			return new Reference(node);
		} finally {
			tx.finish();
		}
	}
	
	public static Transaction getTransaction(){
		return graphDB.beginTx();
	}
	
	private static String createConcatenatedKey(){
		String concatKey = NODE_TYPE + "," + Record.NAME;
		for(RecordKey k: RecordKey.values()){
			concatKey = concatKey + "," + k;
		}
		return concatKey;
	}
	
}
