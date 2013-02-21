package pishen.db.node;

import java.io.File;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pishen.db.NodeShell;
import pishen.db.rel.HasRef;
import pishen.db.rel.RelType;

public class Record extends NodeShell {
	public static final String NAME = "NAME";
	
	//private static final Logger log = Logger.getLogger(Record.class);
	private static final String TEXT_DIR = "text-records";
	private static final String PDF_DIR = "pdf-records";
	
	static{
		//create the dir for text and pdf records
		createDirIfNotExist(PDF_DIR);
		createDirIfNotExist(TEXT_DIR);
	}
	
	public Record(Node node){
		super(node);
	}
	
	public String getName(){
		return super.getStringProperty(NAME);
	}
	
	public void setProperty(RecordKey key, Object value){
		super.setProperty(key.toString(), value);
	}
	
	public void removeProperty(RecordKey key){
		super.removeProperty(key.toString());
	}
	
	public boolean hasProperty(RecordKey key){
		return super.hasProperty(key.toString());
	}
	
	public String getStringProperty(RecordKey key){
		return super.getStringProperty(key.toString());
	}
	
	public boolean getBooleanProperty(RecordKey key){
		return super.getBooleanProperty(key.toString());
	}
	
	public HasRef createHasRefTo(Reference targetRef){
		Relationship rel = super.createRelationshipTo(targetRef, RelType.HAS_REF);
		return new HasRef(rel);
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
