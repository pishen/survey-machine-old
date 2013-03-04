package pishen.db;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.tooling.GlobalGraphOperations;


public class DBHandler {
	private static final Logger log = Logger.getLogger(DBHandler.class);
	//private static final String CONCAT_KEY = createConcatenatedKey();
	private static GraphDatabaseService graphDB;
	//private static ReadableIndex<Node> autoNodeIndex;
	//private static RecordHits allRecordHits;
	//private static int count;
	
	public static void startGraphDB(){
		log.info("starting graph DB");
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("graph-db").newGraphDatabase();
		
		//autoNodeIndex = graphDB.index().getNodeAutoIndexer().getAutoIndex();
		Record.connectNodeIndex();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				log.info("shutting down graphDB");
				graphDB.shutdown();
			}
		});
	}
	
	/*public static void initRecordIterator(){
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
	}*/
	/*
	public static RecordHits getAllRecords(){
		IndexHits<Node> indexHits = autoNodeIndex.get(NODE_TYPE, NodeType.RECORD.toString());
		return new RecordHits(indexHits);
	}
	
	public static RecordHits getRecords(RecordKey key, Object value){
		IndexHits<Node> hits = autoNodeIndex.get(key.toString(), value);
		return new RecordHits(hits);
	}*/
	/*
	public static Record getOrCreateRecord(String recordName){
		Transaction tx = graphDB.beginTx();;
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
	*/
	/*public static Reference createReference(){
		Transaction tx = graphDB.beginTx();
		try {
			Node node = graphDB.createNode();
			node.setProperty(NODE_TYPE, NodeType.REFERENCE.toString());
			tx.success();
			return new Reference(node);
		} finally {
			tx.finish();
		}
	}*/
	
	public static Transaction getTransaction(){
		return graphDB.beginTx();
	}
	
	protected static Iterable<Node> getAllNodes(){
		return GlobalGraphOperations.at(graphDB).getAllNodes();
	}
	
	protected static Node createNode(){
		Transaction tx = graphDB.beginTx();
		try{
			Node newNode = graphDB.createNode(); 
			tx.success();
			return newNode;
		}finally{
			tx.finish();
		}
	}
	
	protected static Index<Node> getOrCreateIndexForNodes(String indexName){
		Transaction tx = graphDB.beginTx();
		try{
			Index<Node> index = graphDB.index().forNodes(indexName);
			tx.success();
			return index;
		}finally{
			tx.finish();
		}
	}
	
	/*
	private static String createConcatenatedKey(){
		String concatKey = NODE_TYPE + "," + Record.NAME;
		for(RecordKey k: RecordKey.values()){
			concatKey = concatKey + "," + k;
		}
		return concatKey;
	}
	*/
}
