package pishen.db;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;

import pishen.core.CitationMark;

public class Record extends NodeShell {
	private static final Logger log = Logger.getLogger(Record.class);
	//type
	private static final String RECORD = "RECORD";
	//DB keys (also used as index keys)
	private static final String NAME = "NAME"; //indexed, required, unique key
	private static final String EE = "EE"; //indexed, required
	private static final String TITLE = "TITLE"; //indexed, required
	private static final String YEAR = "YEAR"; //required
	private static final String EMB = "EMB";
	private static final String REF_FETCHED = "REF_FETCHED"; //default is false
	private static final String CITATION_TYPE = "CITATION_TYPE";
	//directory names
	private static final String TEXT_DIR = "text-records";
	private static final String PDF_DIR = "pdf-records";
	//index name
	private static final String RECORD_INDEX = "RECORD_INDEX";
	//index key
	//private static final String REF_INDEX = "REF_INDEX";
	
	static{
		//create the directories for text and PDF records
		createDirIfNotExist(PDF_DIR);
		createDirIfNotExist(TEXT_DIR);
	}
	
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
	
	public static Record getOrCreateRecord(DBHandler dbHandler, String recordName){
		Node node = dbHandler.getIndexForNodes(RECORD_INDEX).get(NAME, recordName).getSingle();
		if(node == null){
			Transaction tx = dbHandler.getTransaction();
			try{
				//atomic: create the node and initialize it with TYPE and NAME
				log.info("create new Record with name: " + recordName);
				node = dbHandler.createNode();
				dbHandler.setNodeType(node, RECORD);
				Record newRecord = new Record(node, dbHandler, recordName);
				tx.success();
				return newRecord;
			}finally{
				tx.finish();
			}
		}else{
			return new Record(node, dbHandler);
		}
	}
	
	public static RecordHits getAllRecords(DBHandler dbHandler){
		return new RecordHits(dbHandler.getNodesWithType(RECORD), dbHandler);
	}
	
	public static RecordHits getRecordsWithEE(DBHandler dbHandler, URL eeURL){
		return new RecordHits(dbHandler.getIndexForNodes(RECORD_INDEX).get(EE, eeURL.toString()), dbHandler);
	}
	
	/*public static RecordHits getRecordsWithTitle(String title){
		return getRecords(TITLE, title);
	}
	
	private static RecordHits getRecords(String key, Object value){
		IndexHits<Node> hits = recordIndex.get(key, value);
		return new RecordHits(hits);
	}*/
	
	public static class RecordHits implements Iterator<Record>, Iterable<Record>{
		private IndexHits<Node> indexHits;
		private DBHandler dbHandler;

		public RecordHits(IndexHits<Node> nodeIter, DBHandler dbHandler){
			this.indexHits = nodeIter;
			this.dbHandler = dbHandler;
		}
		
		@Override
		public Iterator<Record> iterator() {
			return this;
		}
		
		@Override
		public boolean hasNext() {
			return indexHits.hasNext();
		}

		@Override
		public Record next() {
			return new Record(indexHits.next(), dbHandler);
		}

		@Override
		public void remove() {}
	}
	
	//connect exist Record
	public Record(Node node, DBHandler dbHandler){
		super(node, dbHandler);
	}
	
	//initialize new Record
	public Record(Node node, DBHandler dbHandler, String recordName){
		super(node, dbHandler);
		super.setProperty(NAME, recordName);
		super.setProperty(REF_FETCHED, "false");
		dbHandler.getIndexForNodes(RECORD_INDEX).add(node, NAME, recordName);
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
			throw new UnsupportedOperationException("Stored EE shouldn't be wrong");
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
	
	public void createRefTo(Reference targetReference){
		super.createRelationshipTo(targetReference, RelType.REF);
	}
	
	public ReferenceHits getReferences(Direction direction){
		return new ReferenceHits(node.getRelationships(RelType.REF, direction), direction);
	}

	public class ReferenceHits implements Iterator<Reference>, Iterable<Reference>{
		private Iterator<Relationship> iterator;
		private Direction direction;
		
		public ReferenceHits(Iterable<Relationship> iterable, Direction direction){
			iterator = iterable.iterator();
			this.direction = direction;
		}
		
		@Override
		public Iterator<Reference> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Reference next() {
			if(direction == Direction.OUTGOING){
				return new Reference(iterator.next().getEndNode(), dbHandler);
			}else{
				return new Reference(iterator.next().getStartNode(), dbHandler);
			}
		}

		@Override
		public void remove() {}
	}
	
	private void setCitationType(CitationMark.Type citationMarkType){
		super.setProperty(CITATION_TYPE, citationMarkType.toString());
	}
	
	public CitationMark.Type getCitationType(){
		return CitationMark.Type.valueOf(super.getProperty(CITATION_TYPE));
	}
	
	@SuppressWarnings("unused")
	public void updateCitationType() throws IOException{
		//go through the References of Record and create a checkList 
		ArrayList<Boolean> checkList = new ArrayList<Boolean>();
		for(Reference reference: getReferences(Direction.OUTGOING)){
			checkList.add(new Boolean(false));
		}
		if(checkList.isEmpty()){
			setCitationType(CitationMark.Type.UNKNOWN);
			return;
		}
		
		//get the text file of Record and setup the regex parser
		Path textRecordPath = Paths.get(TEXT_DIR, getName());
		if(Files.notExists(textRecordPath)){
			setCitationType(CitationMark.Type.UNKNOWN);
			return;
		}
		String textRecordContent = new String(Files.readAllBytes(textRecordPath));
		textRecordContent = textRecordContent.replaceAll("\n", "");
		//find "[...]"
		Pattern pattern = Pattern.compile("\\[([^\\[\\]]*)\\]");
		Matcher matcher = pattern.matcher(textRecordContent);
		
		//check the citationMarks (each citationMark contains one or more citations)
		//see if all the numbers in checkList are matched by at least one citation in text
		//and there's no number in text which exceed the checkList's range
		while(matcher.find()){
			String stringInBrackets = matcher.group(1);
			CitationMark mark = new CitationMark(stringInBrackets);
			if(mark.getType() == CitationMark.Type.NUMBER){
				for(String citationString: mark){
					int citationInt = Integer.parseInt(citationString);
					if(citationInt > checkList.size()){
						setCitationType(CitationMark.Type.UNKNOWN);
					}else{
						checkList.set(citationInt - 1, new Boolean(true));
					}
				}
			}
		}
		for(Boolean check: checkList){
			if(check == false){
				setCitationType(CitationMark.Type.UNKNOWN);
			}
		}
		
		setCitationType(CitationMark.Type.NUMBER);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Record)){
			return false;
		}
		Record targetRecord = (Record)obj;
		return this.getName().equals(targetRecord.getName());
	}
	
	private static void createDirIfNotExist(String filename){
		File dir = new File(filename);
		if(!dir.exists()){
			dir.mkdir();
		}
	}
}
