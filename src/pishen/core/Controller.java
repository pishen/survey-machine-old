package pishen.core;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.db.DBHandler;
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
		DBHandler.test();
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
	
	public static void fetchRefForAllRecords(){
		int count = 0;
		for(Record record: DBHandler.getAllRecords()){
			log.info("[FETCH_REF] #" + (++count) + " name=" + record.getName());
			try {
				RuleHandler.getRefFetcher(record).fetchRef();
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
