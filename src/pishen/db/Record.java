package pishen.db;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import pishen.core.CitationMark;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Record extends NodeShell {
	public static enum CitationType{
		NO_MARK, NUMBER, OVERFLOW, UNKNOWN
	}
	
	private static final Logger log = Logger.getLogger(Record.class);
	//node type
	//public static final String TYPE = "RECORD";
	//DB keys (also used as index keys)
	public static final String NAME = "NAME"; //indexed, required, unique key
	private static final String EE = "EE"; //indexed, required
	private static final String TITLE = "TITLE"; //indexed, required
	private static final String YEAR = "YEAR"; //required
	private static final String EMB = "EMB";
	private static final String REF_FETCHED = "REF_FETCHED"; //default is false
	//directory names
	private static final String TEXT_DIR = "text-records";
	private static final String PDF_DIR = "pdf-records";
	//index name
	public static final String RECORD_INDEX = "RECORD_INDEX";
	//index key
	private static final String REF_INDEX = "REF_INDEX";
	
	//private static NodeIndexShell recordIndex;
	private NodeIndexShell singleRecordIndex;
	
	static{
		//create the directories for text and PDF records
		createDirIfNotExist(PDF_DIR);
		createDirIfNotExist(TEXT_DIR);
	}
	
	//called by DBHandler when the database is ready
	/*public static void connectNodeIndex(DBHandler dbHandler){
		recordIndex = new NodeIndexShell(dbHandler.getOrCreateRecordIndex());
	}*/
	
	/*public static void reCreateNodeIndex(){
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
	}*/
	
	/*public static Record getOrCreateRecord(String recordName){
		Node node = recordIndex.get(NAME, recordName).getSingle();
		if(node == null){
			Transaction tx = dbHandler.getTransaction();
			try{
				//atomic: create the node and initialize it
				node = DBHandler.createNode();
				Record newRecord = new Record(node, recordName);
				tx.success();
				return newRecord;
			}finally{
				tx.finish();
			}
		}else{
			return new Record(node);
		}
	}*/
	
	/*public static RecordHits getAllRecords(){
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
	}*/
	
	//connect exist Record
	public Record(Node node, DBHandler dbHandler){
		super(node, dbHandler);
		singleRecordIndex = dbHandler.getIndexForNodes(super.getProperty(NAME));
	}
	
	//initialize new Record
	public Record(Node node, DBHandler dbHandler, String recordName){
		super(node, dbHandler);
		
		super.setProperty(NAME, recordName);
		super.setProperty(REF_FETCHED, "false");
		
		dbHandler.getIndexForNodes(RECORD_INDEX).add(node, NAME, recordName);
		
		singleRecordIndex = dbHandler.getIndexForNodes(recordName);
	}
	
	public String getName(){
		return super.getProperty(NAME);
	}
	
	public void setEE(URL eeURL){
		super.setProperty(EE, eeURL.toString());
		dbHandler.getIndexForNodes(RECORD_INDEX).add(node, EE, eeURL.toString());
	}
	
	public URL getEE(){
		try {
			return new URL(super.getProperty(EE));
		} catch (MalformedURLException e) {
			log.error("EE is not valid", e);
			throw new RecordRuntimeException("Stored EE shouldn't be wrong");
		}
	}
	
	public void setTitle(String title){
		super.setProperty(TITLE, title);
		dbHandler.getIndexForNodes(RECORD_INDEX).add(node, TITLE, title);
	}
	
	public String getTitle(){
		return super.getProperty(TITLE);
	}
	
	public void setYear(int year){
		super.setProperty(YEAR, Integer.toString(year));
	}
	
	public int getYear(){
		return Integer.parseInt(super.getProperty(YEAR));
	}
	
	public void setEmb(boolean value){
		super.setProperty(EMB, Boolean.toString(value));
	}
	
	public boolean getEmb(){
		return Boolean.parseBoolean(super.getProperty(EMB));
	}
	
	public void setRefFetched(boolean value){
		super.setProperty(REF_FETCHED, Boolean.toString(value));
	}
	
	public boolean getRefFetched(){
		return Boolean.parseBoolean(super.getProperty(REF_FETCHED));
	}
	
	public void createRefTo(Reference targetReference, int index){
		super.createRelationshipTo(targetReference, RelType.REF);
		singleRecordIndex.add(targetReference.node, REF_INDEX, Integer.toString(index));
	}
	
	public int getRefCount(){
		int count = 0;
		for(HasRef hasRef: getHasRefs()){
			count++;
		}
		return count;
	}

	public HasRefHits getHasRefs(){
		return new HasRefHits(super.getRelationships(RelType.HAS_REF), dbHandler);
	}
	
	public Cite createCiteTo(Record targetRecord, String citation){
		Transaction tx = dbHandler.getTransaction();
		try{
			Relationship rel = super.createRelationshipTo(targetRecord, RelType.CITE);
			Cite newCite = new Cite(rel, dbHandler, citation); 
			tx.success();
			return newCite;
		}finally{
			tx.finish();
		}
	}
	
	//TODO write CitationType to DB, update it (for UNKNOWN) if the rule change?
	public CitationType getCitationType(){
		File textFile = getTextFile();
		if(textFile.exists() == false){
			return CitationType.UNKNOWN;
		}
		
		if(this.getRefCount() == 0){
			return CitationType.NO_MARK;
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
		
		boolean[] citationCheck = new boolean[getRefCount()];
		boolean hasMark = false;
		
		while(matcher.find()){
			hasMark = true;
			String stringInBrackets = matcher.group(1);
			CitationMark mark = new CitationMark(stringInBrackets);
			if(mark.getType() == CitationMark.Type.NUMBER){
				for(int citation: mark.getIntCitations()){
					if(citation > citationCheck.length){
						return CitationType.OVERFLOW;
					}else{
						citationCheck[citation - 1] = true;
					}
				}
			}
		}
		
		if(hasMark == false){
			return CitationType.NO_MARK;
		}
		
		for(boolean check: citationCheck){
			if(check == false){
				return CitationType.UNKNOWN;
			}
		}
		
		return CitationType.NUMBER;
	}

	public CiteHits getOutgoingCites(){
		return new CiteHits(super.getRelationships(RelType.CITE, Direction.OUTGOING), dbHandler);
	}
	
	public CiteHits getIncomingCites(){
		return new CiteHits(super.getRelationships(RelType.CITE, Direction.INCOMING), dbHandler);
	}
	
	/*public long getId(){
		return node.getId();
	}*/

	/*public void delete(){
		Transaction tx = dbHandler.getTransaction();
		try{
			recordIndex.remove(node);
			super.delete();
			tx.success();
		}finally{
			tx.finish();
		}
	}*/
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Record)){
			return false;
		}
		Record targetRecord = (Record)obj;
		return this.getName().equals(targetRecord.getName());
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
	
	public class RecordRuntimeException extends RuntimeException{
		private static final long serialVersionUID = 1L;
		public RecordRuntimeException(String message){
			super(message);
		}
	}
	
}
