package pishen.core;

import java.util.HashMap;
import java.util.List;

import org.neo4j.graphdb.Direction;

import pishen.db.Record;
import pishen.db.Reference;

public class Cocitation implements RankingAlgo{

	@Override
	public List<Record> rankOn(List<Record> seedRecords, Record blockRecord) {
		HashMap<Record, Integer> countMap = new HashMap<Record, Integer>();
		
		for(Record seedRecord: seedRecords){
			for(Reference ref1: seedRecord.getReferences(Direction.INCOMING)){
				Record newerRecord = ref1.getStartRecord();
				if(newerRecord.getId() != blockRecord.getId() && newerRecord.getCitationType() == CitationMark.Type.NUMBER){
					for(Reference ref2: newerRecord.getReferences(Direction.OUTGOING)){
						Record candidateRecord = ref2.getEndRecord();
						if(candidateRecord != null && candidateRecord.getCitationType() == CitationMark.Type.NUMBER){
							//TODO
						}
					}
				}
			}
		}
		
		return null;
	}
	
	
}
