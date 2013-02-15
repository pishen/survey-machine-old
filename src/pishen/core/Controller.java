package pishen.core;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.db.DBHandler;
import pishen.db.DBRecord;
import pishen.db.DBRecordIterator;
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
	
	//TODO change EMB from String to boolean
	//TODO add property NAME, TYPE for record, copy FILENAME to NAME, delete RECORD_KEY and FILENAME
	//TODO remove property RECORD_KEY and FILENAME from auto_index
	//TODO refactor XMLParser and XMLRecord
	
	public static void test(){
		DBRecordIterator iter = DBHandler.iteratorForRecord();
		while(iter.hasNext()){
			DBRecord dbRecord = iter.next();
			log.info("[TEST] refactoring record: " + dbRecord.getRecordKey());
			dbRecord.refactor();
		}
	}
	
	public static void copyXMLValuesToDB() throws Exception{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			//copy the key-value pairs from XMLRecord to database
			DBRecord dbRecord = DBHandler.getRecordWithKey(xmlRecord.getRecordKey());
			for(RecordKey key: RecordKey.values()){
				dbRecord.setProperty(key, xmlRecord.getProperty(key));
			}
		}
	}
	
	public static void fetchContentsForAllRecords(){
		DBRecordIterator iter = DBHandler.iteratorForRecord();
		while(iter.hasNext()){
			DBRecord dbRecord = iter.next();
			log.info("[FETCH_CONTENT] key=" + dbRecord.getRecordKey());
			ContentFetcher.fetchContent(iter.next());
		}
	}
	
	public static void fetchRefForAllRecords(){
		DBRecordIterator iter = DBHandler.iteratorForRecord();
		while(iter.hasNext()){
			DBRecord dbRecord = iter.next();
			log.info("[FETCH_REF] key=" + dbRecord.getRecordKey());
			try {
				RuleHandler.getRefFetcher(dbRecord).fetchRef();
			} catch (RuleNotFoundException e) {
				log.info("Rule not found");
			}
		}
	}
	
	public static void linkRecords() throws FileNotFoundException, XMLStreamException{
		//TODO change the way of iterating to DB-based
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			DBRecord dbRecord = DBHandler.getRecordWithKey(xmlRecord.getRecordKey());
			try {
				RecordLinker.linkRecord(dbRecord);
			} catch (LinkingFailException e) {
				continue;
			}
		}
		RecordLinker.writeTypeCounts();
	}
	
	//TODO feature require: updating property value by XMLParser and delete the record that's not exist anymore 
	
}
