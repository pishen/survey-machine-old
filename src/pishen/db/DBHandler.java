package pishen.db;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.tooling.GlobalGraphOperations;


public class DBHandler {
	private static final Logger log = Logger.getLogger(DBHandler.class);
	
	private static final String TYPE_INDEX = "TYPE_INDEX";
	private static final String TYPE = "TYPE";
	
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
	public IndexHits<Node> getNodesWithType(String type){
		return typeIndex.get(TYPE, type);
	}
	
	public Node createNode(){
		Transaction tx = graphDB.beginTx();
		try{
			Node node = graphDB.createNode();
			tx.success();
			return node;
		}finally{
			tx.finish();
		}
	}
	
	public Iterable<Node> getAllNodes(){
		return GlobalGraphOperations.at(graphDB).getAllNodes();
	}
	
	//index handling
	public void setNodeType(Node node, String type){
		Transaction tx = graphDB.beginTx();
		try{
			node.setProperty(TYPE, type);
			typeIndex.add(node, TYPE, type);
			tx.success();
		}finally{
			tx.finish();
		}
	}
	
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
