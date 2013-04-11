package pishen.core;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import pishen.db.Cite;
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
		//refactor, remove Cite that pointing to future
		int count = 0;
		for(Record record: Record.getAllRecords()){
			log.info("[TEST] #" + (++count) + " find illegal cite");
			for(Cite cite: record.getOutgoingCites()){
				if(cite.getEndRecord().getYear() > record.getYear()){
					log.info("DELETE");
					cite.delete();
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
	
	public static void connectRecords(){
		int count = 0;
		for(Record record: Record.getAllRecords()){
			log.info("[CONNECT] #" + (++count) + " name=" + record.getName());
			try {
				RuleHandler.getRecordConnector(record).connect();
			} catch (RuleNotFoundException e) {
				log.info("rule not found");
			}
		}
	}
	
	public static void eval(int maxReturn){
		TestCase testCase = TestCase.getSingleTestCase();
		if(testCase == null){
			log.info("[EVAL] TestCase not found.");
			return;
		}
		
		log.info("[EVAL] survey record: " + testCase.getSurveyRecord().getName());
		log.info("[EVAL] test record: " + testCase.getTestRecord().getName());
		log.info("[EVAL] ans size=" + testCase.getAnsSize());
		
		Cocitation cocitation = new Cocitation();
		log.info("[EVAL] computing cocitation");
		ArrayList<Record> rankList = cocitation.rank(testCase, testCase.getAnsSize());
		
		Evaluator evaluator = new Evaluator(testCase);
		log.info("[EVAL] computing F1");
		log.info("[EVAL] precision=" + evaluator.computePrecision(rankList));
		log.info("[EVAL] recall=" + evaluator.computeRecall(rankList));
		double f1 = evaluator.computeF1(rankList);
		log.info("[EVAL] F1=" + f1);
	}
	
}
