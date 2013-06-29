package pishen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import pishen.db.Cite;
import pishen.db.Record;
import pishen.db.RecordHits;

public class TestCase {
	private static final Logger log = Logger.getLogger(TestCase.class);
	private Record baseRecord;
	private List<Record> testingRecords = new ArrayList<Record>();
	private int validNeighborCount; //in&out for WAIM, out for VLDB.
	
	public TestCase(Record baseRecord){
		this.baseRecord = baseRecord;
		for(Cite cite: baseRecord.getOutgoingCites()){
			if(cite.getEndRecord().getCitationType() == Record.CitationType.NUMBER){
				validNeighborCount++;
				testingRecords.add(cite.getEndRecord());
			}
		}
		//turn on for WAIM method
		/*for(Cite cite: baseRecord.getIncomingCites()){
			if(cite.getStartRecord().getCitationType() == Record.CitationType.NUMBER){
				validNeighborCount++;
			}
		}*/
	}
	
	public Record getBaseRecord(){
		return baseRecord;
	}
	
	public int getThresholdYear(){
		return baseRecord.getYear();
	}
	
	public List<Record> getSeedRecords(double seedRatio, int groupIndex){
		int seedSize = (int)(testingRecords.size() * seedRatio);
		List<Record> seedRecords = new ArrayList<Record>();
		int start = seedSize * groupIndex;
		for(int i = start; i < start + seedSize && i < testingRecords.size(); i++){
			seedRecords.add(testingRecords.get(i));
		}
		if(seedRecords.isEmpty()){
			return null;
		}else{
			return seedRecords;
		}
	}
	
	public List<Record> getAnsRecords(double seedRatio, int groupIndex){
		int seedSize = (int)(testingRecords.size() * seedRatio);
		List<Record> ansRecords = new ArrayList<Record>();
		int start = seedSize * groupIndex;
		for(int i = 0; i < testingRecords.size(); i++){
			if(i < start || i >= start + seedSize){
				ansRecords.add(testingRecords.get(i));
			}
		}
		return ansRecords;
	}
	
	private static class BaseRecordComparator implements Comparator<TestCase>{
		@Override
		public int compare(TestCase arg0, TestCase arg1) {
			//descending
			return arg1.validNeighborCount - arg0.validNeighborCount;
		}
	}
	
	/*public static List<TestCase> findTestCases(int topK){
		List<TestCase> testCases = new ArrayList<TestCase>();
		RecordHits allRecords = Record.getAllRecords();
		int count = 0;
		for(Record record: allRecords){
			log.info("Checking record #" + (++count));
			//turn this on for WAIM method
			if(record.getCitationType() != Record.CitationType.NUMBER){
				continue;
			}
			
			testCases.add(new TestCase(record));
			Collections.sort(testCases, new BaseRecordComparator());
			
			if(testCases.size() > topK){
				testCases.remove(testCases.size() - 1);
			}
		}
		count = 0;
		for(TestCase testCase: testCases){
			log.info("Top #" + (++count) + " degree=" + testCase.validNeighborCount + " name=" + testCase.getBaseRecord().getName());
		}
		
		return testCases;
	}*/
	
	/*public static TestCase getSingleTestCase(){
		TestCase testCase = null;
		RecordHits allRecords = Record.getAllRecords();
		int count = 0;
		for(Record record: allRecords){
			log.info("finding TestCase #" + (++count));
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
					if(outCount >= 3 && inCount >= 5){
						log.info("found a TestCase");
						testCase = new TestCase(record, citedRecord);
						allRecords.close();
						return testCase;
					}
				}
			}
		}
		return null;
	}*/
}
