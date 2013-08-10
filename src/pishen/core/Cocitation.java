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
	private Record sourceRecord;
	
	@Override
	public List<Record> rankOn(List<Record> seedRecords, Record sourceRecord) {
		this.sourceRecord = sourceRecord;
		
		final HashMap<Record, Integer> countMap = new HashMap<Record, Integer>();
		
		//start from each seed
		for(Record seedRecord: seedRecords){
			//get middle Records that cite the seed
			for(Reference ref1: seedRecord.getReferences(Direction.INCOMING)){
				Record newerRecord = ref1.getStartRecord();
				//filter the middle Records
				if(isValid(newerRecord)){
					//get Records cited by middle Record
					for(Reference ref2: newerRecord.getReferences(Direction.OUTGOING)){
						Record candidateRecord = ref2.getEndRecord();
						//filter the final Records
						if(isValid(candidateRecord) && !seedRecords.contains(candidateRecord)){
							//increment the count of candidate by 1
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
		
		//sort the rank list
		ArrayList<Record> rankRecords = new ArrayList<Record>(countMap.keySet());
		Collections.sort(rankRecords, new Comparator<Record>(){
			@Override
			public int compare(Record o1, Record o2) {
				return countMap.get(o2) - countMap.get(o1);
			}
		});
		
		return rankRecords;
	}
	
	private boolean isValid(Record record){
		if(record != null &&
				!record.equals(sourceRecord) &&
				record.getYear() <= sourceRecord.getYear() &&
				record.getCitationType() == CitationMark.Type.NUMBER){
			return true;
		}else{
			return false;
		}
	}
}
