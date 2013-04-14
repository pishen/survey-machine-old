package pishen.core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	
	public static void test(int threshold){
		try {
			PrintWriter out = new PrintWriter(new FileWriter("graph-file"));
			
			for(Record record: Record.getAllRecords()){
				log.info("parsing node #" + record.getId());
				if(record.getId() > threshold){
					continue;
				}
				out.print(record.getId());
				boolean empty = true;
				for(Cite cite: record.getOutgoingCites()){
					if(cite.getEndRecord().getId() > threshold){
						continue;
					}
					if(empty){
						empty = false;
						out.print("->" + cite.getEndRecord().getId());
					}else{
						out.print("," + cite.getEndRecord().getId());
					}
				}
				for(Cite cite: record.getIncomingCites()){
					if(cite.getStartRecord().getId() > threshold){
						continue;
					}
					if(empty){
						empty = false;
						out.print("->" + cite.getStartRecord().getId());
					}else{
						out.print("," + cite.getStartRecord().getId());
					}
				}
				out.println();
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
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
		ArrayList<Record> rankList = cocitation.rank(testCase, maxReturn);
		
		Evaluator evaluator = new Evaluator(testCase);
		log.info("[EVAL] computing F1");
		log.info("[EVAL] precision=" + evaluator.computePrecision(rankList));
		log.info("[EVAL] recall=" + evaluator.computeRecall(rankList));
		double f1 = evaluator.computeF1(rankList);
		log.info("[EVAL] F1=" + f1);
		
		int count = 0;
		for(Record record: rankList){
			log.info("[EVAL] rank #" + (++count) + " " + record.getTitle());
		}
	}
	
}
