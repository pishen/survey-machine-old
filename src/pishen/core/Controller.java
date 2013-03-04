package pishen.core;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.db.DBHandler;
import pishen.db.Record;
import pishen.db.RecordHits;
import pishen.exception.LinkingFailException;
import pishen.exception.RuleNotFoundException;
import pishen.xml.XMLParser;
import pishen.xml.XMLRecord;

public class Controller {
	private static final Logger log = Logger.getLogger(Controller.class);
	private static final String XML_FILENAME = "dblp.xml";
	private static RecordHits allRecordsHits;
	
	public static void startGraphDB(){
		DBHandler.startGraphDB();
	}
	
	public static void test(){
		
	}
	
	public static void copyDBLPInfo() throws Exception{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextValidXMLRecord();
			//copy the key-value pairs from XMLRecord to database
			Record record = Record.getOrCreateRecord(xmlRecord.getName());
			xmlRecord.dumpTo(record);
		}
	}
	
	public static void fetchContentsForAllRecords(){
		int count = 0;
		for(Record record: Record.getAllRecords()){
			log.info("[FETCH_CONTENT] #" + (++count) + " name=" + record.getName());
			ContentFetcher.fetchContent(record);
		}
	}
	
	public static void fetchRefForAllRecords(int numOfThreads){
		allRecordsHits = Record.getAllRecords();
		for(int i = 1; i <=numOfThreads; i++){
			new Thread("t" + i){
				@Override
				public void run() {
					try{
						Record record = null;
						while((record = allRecordsHits.getNextRecord()) != null){
							log.info("[FETCH_REF] #" + allRecordsHits.count() + " name=" + record.getName());
							try {
								RuleHandler.getRefFetcher(record).fetchRef();
							} catch (RuleNotFoundException e) {
								log.info("Rule not found");
							}
						}
					}catch(RuntimeException e){
						log.error("Runtime error", e);
						System.exit(0);
					}
				}
			}.start();
		}
	}
	
	public static void linkRecords() throws FileNotFoundException, XMLStreamException{
		//TODO change the way of iterating to DB-based
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextValidXMLRecord();
			Record record = Record.getOrCreateRecord(xmlRecord.getName());
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
