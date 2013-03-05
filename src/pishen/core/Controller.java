package pishen.core;

import org.apache.log4j.Logger;

import pishen.db.DBHandler;
import pishen.db.Record;
import pishen.db.RecordHits;
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
		log.info("[TEST]");
		int count = 0;
		for(Record record: Record.getAllRecords()){
			log.info("[CHECK] #" + (++count) + " NAME=" + record.getName());
			if(!record.getTextFile().exists()){
				log.info("NO-TEXT");
			}else{
				if(record.getHasRefCount() == 0){
					log.info("HAS-TEXT NO-REF");
				}else{
					boolean emb;
					if(record.isEmb() == null || record.isEmb() == false){
						emb = false;
					}else{
						emb = true;
					}
					Record.CitationType citationType = record.getCitationType();
					if(citationType == null){
						if(emb){
							log.info("HAS-TEXT HAS-REF HAS-EMB NULL");
						}else{
							log.info("HAS-TEXT HAS-REF NO-EMB NULL");
						}
					}else if(citationType == Record.CitationType.NUMBER){
						if(emb){
							log.info("HAS-TEXT HAS-REF HAS-EMB NUMBER");
						}else{
							log.info("HAS-TEXT HAS-REF NO-EMB NUMBER");
						}
					}else if(citationType == Record.CitationType.TEXT){
						if(emb){
							log.info("HAS-TEXT HAS-REF HAS-EMB TEXT");
						}else{
							log.info("HAS-TEXT HAS-REF NO-EMB TEXT");
						}
					}else{
						if(emb){
							log.info("HAS-TEXT HAS-REF HAS-EMB UNKNOWN");
						}else{
							log.info("HAS-TEXT HAS-REF NO-EMB UNKNOWN");
						}
					}
				}
			}
		}
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
	
	/*public static void linkRecords() throws FileNotFoundException, XMLStreamException{
		//TODO change the way of iterating to DB-based
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextValidXMLRecord();
			Record record = Record.getOrCreateRecord(xmlRecord.getName());
			try {
				CitationMark.linkRecord(record);
			} catch (LinkingFailException e) {
				continue;
			}
		}
		CitationMark.writeTypeCounts();
	}*/
	
	//TODO feature require: updating property value by XMLParser and delete the record that's not exist anymore 
	
}
