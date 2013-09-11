package pishen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.neo4j.graphdb.Direction;

import pishen.db.Record;
import pishen.db.Reference;

public class TestCase {
	//private static final Logger log = Logger.getLogger(TestCase.class);
	
	private List<Record> ansRecords;
	private List<Record> seedRecords;
	private Record sourceRecord;
	private List<Record> cocitationRankRecords;
	private List<Record> katzRankRecords;
	
	public static List<TestCase> createTestCaseList(Record sourceRecord, double hidingRatio, int minReferenceSize){
		//find the Records referenced by sourceRecord
		List<Record> base = new ArrayList<Record>(); 
		for(Reference ref: sourceRecord.getReferences(Direction.OUTGOING)){
			Record referencedRecord = ref.getEndRecord();
			if(referencedRecord != null && referencedRecord.getCitationType() == CitationMark.Type.NUMBER){
				base.add(referencedRecord);
			}
		}
		
		if(base.size() < minReferenceSize){
			return null;
		}
		
		List<TestCase> testCases = new ArrayList<TestCase>();
		
		int numOfAns = (int)(base.size() * hidingRatio);
		//create multiple TestCases as cross validation
		for(int i = 0; i < base.size() / numOfAns; i++){
			//log.info("Create testCase " + i + " for source " + sourceRecord.getName());
			//randomly pick numOfAns Records as answer, others as seeds
			List<Record> copy = new ArrayList<Record>(base);
			Collections.shuffle(copy);
			List<Record> ansRecords = copy.subList(0, numOfAns);
			List<Record> seedRecords = copy.subList(numOfAns, copy.size());
			testCases.add(new TestCase(ansRecords, seedRecords, sourceRecord));
		}
		
		return testCases;
	}
	
	public TestCase(List<Record> ansRecords, List<Record> seedRecords, Record sourceRecord){
		this.ansRecords = ansRecords;
		this.seedRecords = seedRecords;
		this.sourceRecord = sourceRecord;
	}
	
	public void computeRankForCocitation(){
		cocitationRankRecords = new Cocitation().rankOn(seedRecords, sourceRecord);
	}
	
	public void computeRankForKatz(int searchingDepth, double decay){
		katzRankRecords = new Katz(searchingDepth, decay).rankOn(seedRecords, sourceRecord);
	}
	
	public List<Record> getAnsRecords(){
		return ansRecords;
	}
	
	public List<Record> getRankRecords(RankingAlgo.Type type){
		if(type == RankingAlgo.Type.Cocitation){
			return cocitationRankRecords;
		}else if(type == RankingAlgo.Type.Katz){
			return katzRankRecords;
		}else{
			return null;
		}
	}
	
}
