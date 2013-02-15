package pishen.db;

import java.io.File;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import pishen.core.RecordKey;


public class DBRecord {
	public static final String RECORD_KEY = "RECORD_KEY"; //TODO clean
	public static final String NAME = "NAME";
	
	private static final Logger log = Logger.getLogger(DBRecord.class);
	private static final String TEXT_DIR = "text-records";
	private static final String PDF_DIR = "pdf-records";
	private static GraphDatabaseService graphDB;
	private Node node;
	
	static{
		//create the dir for text and pdf records
		createDirIfNotExist(PDF_DIR);
		createDirIfNotExist(TEXT_DIR);
	}
	
	public static void setGraphDB(GraphDatabaseService graphDB){
		DBRecord.graphDB = graphDB;
	}
	
	public DBRecord(Node node){
		this.node = node;
	}
	
	//TODO clean
	public String getRecordKey(){
		return (String)node.getProperty(RECORD_KEY);
	}
	
	//TODO clean
	public void refactor(){
		Transaction tx = graphDB.beginTx();
		try {
			node.removeProperty(RECORD_KEY);
			node.setProperty(NAME, getStringProperty(RecordKey.FILENAME));
			node.removeProperty(RecordKey.FILENAME.toString());
			node.setProperty(DBHandler.NODE_TYPE, NodeType.RECORD.toString());
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public void setProperty(RecordKey key, Object value){
		if(value != null){
			Transaction tx = graphDB.beginTx();
			try {
				node.setProperty(key.toString(), value);
				tx.success();
			} finally {
				tx.finish();
			}
		}
	}
	
	public void removeProperty(RecordKey key){
		Transaction tx = graphDB.beginTx();
		try {
			node.removeProperty(key.toString());
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public boolean hasProperty(RecordKey key){
		return node.hasProperty(key.toString());
	}
	
	public String getStringProperty(RecordKey key){
		return (String)getProperty(key);
	}
	
	public boolean getBooleanProperty(RecordKey key){
		return (Boolean)getProperty(key);
	}
	
	public Object getProperty(RecordKey key){
		return node.getProperty(key.toString());
	}
	
	public File getPDFFile(){
		return new File(PDF_DIR + "/" + getStringProperty(RecordKey.FILENAME) + ".pdf");
	}
	
	public File getTextFile(){
		return new File(TEXT_DIR + "/" + getStringProperty(RecordKey.FILENAME));
	}
	
	private static void createDirIfNotExist(String filename){
		File dir = new File(filename);
		if(!dir.exists()){
			dir.mkdir();
		}
	}
	
}
