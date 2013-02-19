package pishen.core;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.db.DBHandler;
import pishen.db.DBRecord;
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
	
	public static void test(){
		int count = 0;
		for(DBRecord dbRecord: DBHandler.getAllRecords()){
			log.info("[TEST] #" + (++count) + " name=" + dbRecord.getName());
			//dbRecord.refactor();
			if(dbRecord.hasProperty(RecordKey.EMB)){
				log.info("changing EMB");
				String emb = dbRecord.getStringProperty(RecordKey.EMB);
				if(emb.equals("yes")){
					dbRecord.setProperty(RecordKey.EMB, true);
				}else{
					dbRecord.setProperty(RecordKey.EMB, false);
				}
			}
		}
	}
	
	public static void copyDBLPInfo() throws Exception{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			//copy the key-value pairs from XMLRecord to database
			DBRecord dbRecord = DBHandler.getOrCreateRecord(xmlRecord.getRecordName());
			for(RecordKey key: RecordKey.values()){
				dbRecord.setProperty(key, xmlRecord.getProperty(key));
			}
		}
	}
	
	public static void fetchContentsForAllRecords(){
		int count = 0;
		for(DBRecord dbRecord: DBHandler.getAllRecords()){
			log.info("[FETCH_CONTENT] #" + (++count) + " name=" + dbRecord.getName());
			ContentFetcher.fetchContent(dbRecord);
		}
	}
	
	public static void fetchRefForAllRecords(){
		int count = 0;
		for(DBRecord dbRecord: DBHandler.getAllRecords()){
			log.info("[FETCH_REF] #" + (++count) + " name=" + dbRecord.getName());
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
			DBRecord dbRecord = DBHandler.getOrCreateRecord(xmlRecord.getRecordName());
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
