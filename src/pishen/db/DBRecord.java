package pishen.db;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import pishen.core.Key;


public class DBRecord {
	public static final String RECORD_KEY = "RECORD_KEY"; 
	
	private static final String TEXT_DIR = "text-records";
	private static final String PDF_DIR = "pdf-records";
	private static final String REF_DIR = "refs";
	private static GraphDatabaseService graphDB;
	private Node node;
	
	static{
		//create the dir for text and pdf records
		createDirIfNotExist(PDF_DIR);
		createDirIfNotExist(TEXT_DIR);
		createDirIfNotExist(REF_DIR);
	}
	
	public static void setGraphDB(GraphDatabaseService graphDB){
		DBRecord.graphDB = graphDB;
	}
	
	public DBRecord(Node node){
		this.node = node;
	}
	
	public String getRecordKey(){
		return (String)node.getProperty(RECORD_KEY);
	}
	
	public void setProperty(Key key, Object value){
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
	
	public boolean hasProperty(Key key){
		return node.hasProperty(key.toString());
	}
	
	public String getStringProperty(Key key){
		return (String)getProperty(key);
	}
	
	public boolean getBooleanProperty(Key key){
		return (Boolean)getProperty(key);
	}
	
	public Object getProperty(Key key){
		return node.getProperty(key.toString());
	}
	
	public File getPDFFile(){
		return new File(PDF_DIR + "/" + getStringProperty(Key.FILENAME) + ".pdf");
	}
	
	public File getTextFile(){
		return new File(TEXT_DIR + "/" + getStringProperty(Key.FILENAME));
	}
	
	public File getRefFile(){
		return new File(REF_DIR + "/" + getStringProperty(Key.FILENAME));
	}
	
	private static void createDirIfNotExist(String filename){
		File dir = new File(filename);
		if(!dir.exists()){
			dir.mkdir();
		}
	}
	
}
