package pishen.core;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.db.DBHandler;
import pishen.db.NodeShell;
import pishen.db.node.Record;
import pishen.db.node.RecordKey;
import pishen.exception.LinkingFailException;
import pishen.exception.RuleNotFoundException;
import pishen.xml.XMLParser;
import pishen.xml.XMLRecord;

public class Controller {
	private static final Logger log = Logger.getLogger(Controller.class);
	private static final String XML_FILENAME = "dblp.xml";
	
	public static void startGraphDB(){
		DBHandler.startGraphDB();
	}
	
	public static void test(){
		int count = 0;
		int noPropertyCount = 0;
		int needFixCount = 0;
		for(Record record: DBHandler.getAllRecords()){
			if(record == null){
				log.info("[FETCH_REF_TEST] #" + (++count) + " wrong");
				continue;
			}
			log.info("[FETCH_REF_TEST] #" + (++count) + " name=" + record.getName());
			try {
				RuleHandler.getRefFetcher(record).fetchRef();
			} catch (RuleNotFoundException e) {
				log.info("Rule not found");
			}
			if(!record.hasProperty(RecordKey.HAS_REF)){
				noPropertyCount++;
			}else if(record.getBooleanProperty(RecordKey.HAS_REF) == true && record.getHasRefCount() == 0){
				needFixCount++;
			}
		}
		log.info("[FETCH_REF_TEST] # has no HAS_REF: " + noPropertyCount);
		log.info("[FETCH_REF_TEST] # need to be fix: " + needFixCount);
	}
	
	public static void copyDBLPInfo() throws Exception{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			//copy the key-value pairs from XMLRecord to database
			Record record = DBHandler.getOrCreateRecord(xmlRecord.getRecordName());
			for(RecordKey key: RecordKey.values()){
				record.setProperty(key, xmlRecord.getProperty(key));
			}
		}
	}
	
	public static void fetchContentsForAllRecords(){
		int count = 0;
		for(Record record: DBHandler.getAllRecords()){
			log.info("[FETCH_CONTENT] #" + (++count) + " name=" + record.getName());
			ContentFetcher.fetchContent(record);
		}
	}
	
	public static void fetchRefForAllRecords(int numOfThreads){
		DBHandler.initRecordIterator();
		for(int i = 1; i <=numOfThreads; i++){
			new Thread("t" + i){
				@Override
				public void run() {
					Record record = null;
					while((record = DBHandler.getNextRecord()) != null){
						log.info("[FETCH_REF] #" + DBHandler.count() + " name=" + record.getName());
						try {
							RuleHandler.getRefFetcher(record).fetchRef();
						} catch (RuleNotFoundException e) {
							log.info("Rule not found");
						}
					}
				}
			}.start();
		}
	}
	
	public static void linkRecords() throws FileNotFoundException, XMLStreamException{
		//TODO change the way of iterating to DB-based
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			Record record = DBHandler.getOrCreateRecord(xmlRecord.getRecordName());
			try {
				RecordLinker.linkRecord(record);
			} catch (LinkingFailException e) {
				continue;
			}
		}
		RecordLinker.writeTypeCounts();
	}
	
	//TODO feature require: updating property value by XMLParser and delete the record that's not exist anymore 
	
}
