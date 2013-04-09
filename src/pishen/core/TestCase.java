package pishen.core;

import org.apache.log4j.Logger;

import pishen.db.Cite;
import pishen.db.Record;
import pishen.db.RecordHits;

public class TestCase {
	private static final Logger log = Logger.getLogger(TestCase.class);
	private Record surveyRecord;
	private Record testRecord;
	
	public TestCase(Record surveyRecord, Record testRecord){
		this.surveyRecord = surveyRecord;
		this.testRecord = testRecord;
	}
	
	public Record getSurveyRecord(){
		return surveyRecord;
	}
	
	public Record getTestRecord(){
		return testRecord;
	}
	
	public int getThresholdYear(){
		return surveyRecord.getYear();
	}
	
	public static TestCase getSingleTestCase(){
		TestCase testCase = null;
		RecordHits allRecords = Record.getAllRecords();
		for(Record record: allRecords){
			int citeCount = 0;
			for(@SuppressWarnings("unused") Cite cite: record.getOutgoingCites()){
				citeCount++;
			}
			if(citeCount >= 40){
				for(Cite cite: record.getOutgoingCites()){
					Record citedRecord = cite.getEndRecord();
					int outCount = 0;
					for(@SuppressWarnings("unused") Cite outCite: citedRecord.getOutgoingCites()){
						outCount++;
					}
					int inCount = 0;
					for(Cite inCite: citedRecord.getIncomingCites()){
						if(inCite.getStartRecord().getYear() <= record.getYear()){
							inCount++;
						}
					}
					//citedRecord may not be NUMBER if outCount == 0
					if(outCount >= 3 && inCount >= 3){
						log.info("found a TestCase");
						testCase = new TestCase(record, citedRecord);
						allRecords.close();
						return testCase;
					}
				}
			}
		}
		return null;
	}
}
