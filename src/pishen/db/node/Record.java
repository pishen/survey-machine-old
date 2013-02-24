package pishen.db.node;

import java.io.File;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pishen.db.NodeShell;
import pishen.db.rel.HasRef;
import pishen.db.rel.HasRefHits;
import pishen.db.rel.RelType;

public class Record extends NodeShell {
	public static final String NAME = "NAME";
	
	//private static final Logger log = Logger.getLogger(Record.class);
	private static final String HAS_REF_COUNT = "HAS_REF_COUNT";
	private static final String TEXT_DIR = "text-records";
	private static final String PDF_DIR = "pdf-records";
	
	static{
		//create the dir for text and pdf records
		createDirIfNotExist(PDF_DIR);
		createDirIfNotExist(TEXT_DIR);
	}
	
	public Record(Node node){
		super(node);
		if(super.hasProperty(HAS_REF_COUNT) == false){
			countHasRef();
		}
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
		super.setProperty(HAS_REF_COUNT, super.getIntProperty(HAS_REF_COUNT) + 1);
		return new HasRef(rel);
	}
	
	public HasRefHits getHasRefs(){
		return new HasRefHits(super.getRelationships(RelType.HAS_REF));
	}
	
	public int getHasRefCount(){
		return super.getIntProperty(HAS_REF_COUNT);
	}
	
	public void delete(){
		super.delete();
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
	
	private void countHasRef(){
		int count = 0;
		for(@SuppressWarnings("unused") HasRef hasRef: getHasRefs()){
			count++;
		}
		super.setProperty(HAS_REF_COUNT, count);
	}
	
}
