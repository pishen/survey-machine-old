package pishen.db;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.IndexHits;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import pishen.core.CitationMark;
import pishen.exception.IllegalOperationException;

public class Record extends NodeShell {
	public static enum CitationType{
		NUMBER, TEXT, UNKNOWN
	}
	
	private static final Logger log = Logger.getLogger(Record.class);
	//node type
	private static final String TYPE = "RECORD";
	//DB keys (also used as index keys)
	private static final String NAME = "NAME"; //indexed, required, unique
	private static final String EE = "EE"; //indexed, required
	private static final String TITLE = "TITLE"; //indexed, required
	private static final String YEAR = "YEAR"; //required
	private static final String EMB = "EMB";
	private static final String HAS_REF = "HAS_REF";
	private static final String HAS_REF_COUNT = "HAS_REF_COUNT"; //required
	//directory names
	private static final String TEXT_DIR = "text-records";
	private static final String PDF_DIR = "pdf-records";
	//index name
	private static final String INDEX_NAME = "RECORD_INDEX";
	//index key
	private static final String IS_RECORD = "IS_RECORD"; //indexed, required, value=true
	
	private static NodeIndexShell recordIndex;
	
	private Node node;
	
	static{
		//create the directories for text and PDF records
		createDirIfNotExist(PDF_DIR);
		createDirIfNotExist(TEXT_DIR);
	}
	
	public static void connectNodeIndex(){
		recordIndex = new NodeIndexShell(DBHandler.getOrCreateIndexForNodes(Record.INDEX_NAME));
	}
	
	public static void reCreateNodeIndex(){
		recordIndex.delete();
		connectNodeIndex();
		int count = 0;
		for(Node node: DBHandler.getAllNodes()){
			log.info("[RE-CREATE] check node #" + (++count));
			try{
				Record record = new Record(node);
				recordIndex.add(node, IS_RECORD, true);
				recordIndex.add(node, NAME, record.getName());
				recordIndex.add(node, EE, record.getEE().toString());
				recordIndex.add(node, TITLE, record.getTitle());
				log.info("index added");
			}catch(IllegalOperationException e){
				log.info("not record");
			}
		}
	}
	
	public static Record getOrCreateRecord(String recordName){
		Node node = recordIndex.get(NAME, recordName).getSingle();
		if(node == null){
			node = DBHandler.createNode();
			return new Record(node, recordName);
		}else{
			return new Record(node);
		}
	}
	
	public static RecordHits getAllRecords(){
		return getRecords(IS_RECORD, true);
	}
	
	public static RecordHits getRecordsWithEE(String ee){
		return getRecords(EE, ee);
	}
	
	public static RecordHits getRecordsWithTitle(String title){
		return getRecords(TITLE, title);
	}
	
	private static RecordHits getRecords(String key, Object value){
		IndexHits<Node> hits = recordIndex.get(key, value);
		return new RecordHits(hits);
	}
	
	public Record(Node node){
		//connect already exists Record
		super(node);
		this.node = node;
		//TODO add more detailed checking if needed
		if(!super.hasType() || !super.getType().equals(Record.TYPE)){
			throw new IllegalOperationException("[RECORD_CONNECT] TYPE is wrong");
		}
	}
	
	public Record(Node node, String name){
		//initialize the node to Record
		super(node);
		this.node = node;
		//make sure the new node is clean
		if(!super.isEmpty()){
			throw new IllegalOperationException("[RECORD_INIT] node is not empty");
		}
		super.setType(TYPE);
		super.setProperty(NAME, name);
		super.setProperty(HAS_REF_COUNT, 0);
		
		recordIndex.add(node, IS_RECORD, true); //default key-value pair for all Records
		recordIndex.add(node, NAME, name);
	}
	
	public CitationType getCitationType(){
		File textFile = getTextFile();
		if(textFile.exists() == false){
			throw new IllegalOperationException("check existence of textfile before calling");
		}
		
		String recordContent;
		try {
			recordContent = Files.toString(textFile, Charsets.UTF_8);
		} catch (IOException e) {
			log.error("error when reading textfile", e);
			return null;
		}
		Pattern pattern = Pattern.compile("\\[([^\\[\\]]*)\\]");
		Matcher matcher = pattern.matcher(recordContent);
		
		CitationType citationType = null;
		
		while(matcher.find()){
			String stringInBrackets = matcher.group(1);
			if(citationType == null){
				citationType = CitationMark.typeOf(stringInBrackets);
			}else if(citationType != CitationMark.typeOf(stringInBrackets)){
				citationType = CitationType.UNKNOWN;
				break;
			}
		}
		
		return citationType;
	}
	
	public String getName(){
		return super.getStringProperty(NAME);
	}
	
	public void setEE(URL eeURL){
		super.setProperty(EE, eeURL.toString());
		recordIndex.add(node, EE, eeURL.toString());
	}
	
	public URL getEE(){
		try {
			return new URL(super.getStringProperty(EE));
		} catch (MalformedURLException e) {
			log.error("EE is not valid", e);
			throw new IllegalOperationException("Stored EE shouldn't be wrong");
		}
	}
	
	public void setTitle(String title){
		super.setProperty(TITLE, title);
		recordIndex.add(node, TITLE, title);
	}
	
	public String getTitle(){
		return super.getStringProperty(TITLE);
	}
	
	public void setYear(String year){
		super.setProperty(YEAR, year);
	}
	
	public int getYear(){
		return Integer.parseInt(super.getStringProperty(YEAR));
	}
	
	public void setEmb(boolean emb){
		super.setProperty(EMB, emb);
	}
	
	public Boolean isEmb(){
		if(super.hasProperty(EMB)){
			return super.getBooleanProperty(EMB);
		}else{
			return null;
		}
	}
	
	public void setHasRef(boolean hasRef){
		super.setProperty(HAS_REF, hasRef);
	}
	
	public Boolean getHasRef(){
		if(super.hasProperty(HAS_REF)){
			return super.getBooleanProperty(HAS_REF);
		}else{
			return null;
		}
	}
	
	public int getHasRefCount(){
		return super.getIntProperty(HAS_REF_COUNT);
	}
	
	public HasRef createHasRefTo(Reference targetRef){
		Relationship rel = super.createRelationshipTo(targetRef, RelType.HAS_REF);
		super.setProperty(HAS_REF_COUNT, super.getIntProperty(HAS_REF_COUNT) + 1);
		return new HasRef(rel);
	}
	
	public HasRefHits getHasRefs(){
		return new HasRefHits(super.getRelationships(RelType.HAS_REF));
	}
	
	public void delete(){
		recordIndex.remove(node);
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
	//deprecated
	/*
	private void countHasRef(){
		int count = 0;
		for(@SuppressWarnings("unused") HasRef hasRef: getHasRefs()){
			count++;
		}
		super.setProperty(HAS_REF_COUNT, count);
	}
	*/
}
