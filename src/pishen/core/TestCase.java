package pishen.core;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;

import pishen.db.Record;
import pishen.db.Reference;

public class TestCase {
	private List<Record> ansList;
	private List<Record> rankList;
	
	public static List<TestCase> createTestCaseList(Record sourceRecord, double hidingRatio){
		//find the Records referenced by sourceRecord
		List<Record> referencedBySource = new ArrayList<Record>(); 
		for(Reference ref: sourceRecord.getReferences(Direction.OUTGOING)){
			Record referencedRecord = ref.getEndRecord();
			if(referencedRecord != null && referencedRecord.getCitationType() == CitationMark.Type.NUMBER){
				referencedBySource.add(referencedRecord);
			}
		}
		//TODO
		return null;
	}
	
	public TestCase(List<Record> testList, List<Record> ansList){
		this.ansList = ansList;
		
	}
}
