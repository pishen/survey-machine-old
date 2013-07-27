package pishen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.neo4j.graphdb.Direction;

import pishen.db.Record;
import pishen.db.Reference;

public class Cocitation implements RankingAlgo{

	@Override
	public List<Record> rankOn(List<Record> seedRecords, Record sourceRecord) {
		final HashMap<Record, Integer> countMap = new HashMap<Record, Integer>();
		
		for(Record seedRecord: seedRecords){
			for(Reference ref1: seedRecord.getReferences(Direction.INCOMING)){
				Record newerRecord = ref1.getStartRecord();
				if(newerRecord.equals(sourceRecord) == false //fileter out sourceRecord
						&& newerRecord.getCitationType() == CitationMark.Type.NUMBER){
					for(Reference ref2: newerRecord.getReferences(Direction.OUTGOING)){
						Record candidateRecord = ref2.getEndRecord();
						if(candidateRecord != null && 
								candidateRecord.getCitationType() == CitationMark.Type.NUMBER &&
								//filter out sourceRecord
								candidateRecord.equals(sourceRecord) == false && 
								seedRecords.contains(candidateRecord) == false &&
								candidateRecord.getYear() < sourceRecord.getYear()){
							if(countMap.containsKey(candidateRecord)){
								countMap.put(candidateRecord, countMap.get(candidateRecord).intValue() + 1);
							}else{
								countMap.put(candidateRecord, 1);
							}
						}
					}
				}
			}
		}
		
		ArrayList<Record> rankRecords = new ArrayList<Record>(countMap.keySet());
		Collections.sort(rankRecords, new Comparator<Record>(){
			@Override
			public int compare(Record o1, Record o2) {
				return countMap.get(o2) - countMap.get(o1);
			}
		});
		
		return rankRecords;
	}
	
	
}
