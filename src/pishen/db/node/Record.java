package pishen.db.node;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;




public class Record {
	public static final String NAME = "NAME";
	
	//private static final Logger log = Logger.getLogger(Record.class);
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
		Record.graphDB = graphDB;
	}
	
	public Record(Node node){
		this.node = node;
	}
	
	public String getName(){
		return (String)node.getProperty(NAME);
	}
	
	//TODO clean
	public void refactor(){
		Transaction tx = graphDB.beginTx();
		try {
			//TODO change EMB from yes/no to true/false
			
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
		return new File(PDF_DIR + "/" + getName() + ".pdf");
	}
	
	public File getTextFile(){
		return new File(TEXT_DIR + "/" + getName());
	}
	
	private static void createDirIfNotExist(String filename){
		File dir = new File(filename);
		if(!dir.exists()){
			dir.mkdir();
		}
	}
	
}
