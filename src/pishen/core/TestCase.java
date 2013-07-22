package pishen.core;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;

import pishen.db.Record;
import pishen.db.Reference;

public class TestCase {
	private List<Record> ansRecords;
	private List<Record> rankRecords;
	
	public static List<TestCase> createTestCaseList(Record sourceRecord, double hidingRatio){
		//find the Records referenced by sourceRecord
		List<Record> referencedBySource = new ArrayList<Record>(); 
		for(Reference ref: sourceRecord.getReferences(Direction.OUTGOING)){
			Record referencedRecord = ref.getEndRecord();
			if(referencedRecord != null && referencedRecord.getCitationType() == CitationMark.Type.NUMBER){
				referencedBySource.add(referencedRecord);
			}
		}
		
		//only create one TestCase for now
		List<Record> ansRecords = new ArrayList<Record>();
		List<Record> seedRecords = new ArrayList<Record>();
		int numOfAns = (int)(referencedBySource.size() * hidingRatio);
		for(int i = 0; i < referencedBySource.size(); i++){
			if(i < numOfAns){
				ansRecords.add(referencedBySource.get(i));
			}else{
				seedRecords.add(referencedBySource.get(i));
			}
		}
		TestCase testCase = new TestCase(ansRecords, seedRecords);
		
		List<TestCase> testCases = new ArrayList<TestCase>();
		testCases.add(testCase);
		return testCases;
	}
	
	public TestCase(List<Record> ansRecords, List<Record> seedRecords){
		this.ansRecords = ansRecords;
		
	}
}
