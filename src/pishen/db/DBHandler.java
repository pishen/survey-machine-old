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
	
	private static final String TYPE_INDEX = "TYPE_INDEX";
	private static final String TYPE = "TYPE";
	private static final String RECORD = "RECORD";
	private static final String REFERENCE = "REFERENCE";
	
	private GraphDatabaseService graphDB;

	private NodeIndexShell typeIndex;
	
	public DBHandler(final String dbName){
		log.info("starting DB: " + dbName);
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(dbName).newGraphDatabase();
		
		//Record.connectNodeIndex(this);
		typeIndex = getIndexForNodes(TYPE_INDEX);
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				log.info("shutting down DB: " + dbName);
				graphDB.shutdown();
			}
		});
	}
	
	public Transaction getTransaction(){
		return graphDB.beginTx();
	}
	
	//node handling
	public Record getOrCreateRecord(String recordName){
		Node node = getIndexForNodes(Record.RECORD_INDEX).get(Record.NAME, recordName).getSingle();
		if(node == null){
			Transaction tx = graphDB.beginTx();
			try{
				//atomic: create the node and initialize it with TYPE and NAME
				log.info("create new Record with name: " + recordName);
				
				node = graphDB.createNode();
				
				node.setProperty(TYPE, RECORD);
				typeIndex.add(node, TYPE, RECORD);
				
				Record newRecord = new Record(node, this, recordName);
				
				tx.success();
				return newRecord;
			}finally{
				tx.finish();
			}
		}else{
			return new Record(node, this);
		}
	}
	
	public Reference createReference(){
		Transaction tx = graphDB.beginTx();
		try{
			Node node = graphDB.createNode();
			node.setProperty(TYPE, REFERENCE);
			typeIndex.add(node, TYPE, REFERENCE);
			Reference newReference = new Reference(node, this);
			tx.success();
			return newReference;
		}finally{
			tx.finish();
		}
	}
	
	public Iterable<Node> getAllNodes(){
		return GlobalGraphOperations.at(graphDB).getAllNodes();
	}
	
	//index handling
	public NodeIndexShell getIndexForNodes(String indexName){
		Transaction tx = graphDB.beginTx();
		try{
			Index<Node> index = graphDB.index().forNodes(indexName);
			tx.success();
			return new NodeIndexShell(index, this);
		}finally{
			tx.finish();
		}
	}
	
}
