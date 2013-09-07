package pishen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.neo4j.graphdb.Direction;

import pishen.db.Record;
import pishen.db.Reference;

public class Katz implements RankingAlgo{
	private int maxDepth;
	private double decay;
	private Record sourceRecord;
	private List<Record> seedRecords;
	private HashMap<Record, Double> rankMap;
	
	public Katz(int searchingDepth, double decay){
		maxDepth = searchingDepth;
		this.decay = decay;
	}
	
	@Override
	public List<Record> rankOn(List<Record> seedRecords, Record sourceRecord) {
		this.sourceRecord = sourceRecord;
		this.seedRecords = seedRecords;
		rankMap = new HashMap<Record, Double>();
		
		for(Record seed: seedRecords){
			searchOn(seed, 0);
		}
		
		//sort the rank list
		ArrayList<Record> rankRecords = new ArrayList<Record>(rankMap.keySet());
		Collections.sort(rankRecords, new Comparator<Record>(){
			@Override
			public int compare(Record o1, Record o2) {
				if(rankMap.get(o2) - rankMap.get(o1) > 0){
					return 1;
				}else if(rankMap.get(o2) - rankMap.get(o1) < 0){
					return -1;
				}else{
					return 0;
				}
			}
		});
		
		return rankRecords;
	}
	
	private void searchOn(Record target, int depth){
		if(depth > 0 && !seedRecords.contains(target)){
			//increment the ranking value of target
			if(rankMap.containsKey(target)){
				rankMap.put(target, rankMap.get(target).doubleValue() + Math.pow(decay, depth));
			}else{
				rankMap.put(target, Math.pow(decay, depth));
			}
		}
		
		//going down to the next depth
		if(depth < maxDepth){
			for(Reference ref: target.getReferences(Direction.INCOMING)){
				Record next = ref.getStartRecord();
				if(isValid(next)){
					searchOn(next, depth + 1);
				}
			}
			for(Reference ref: target.getReferences(Direction.OUTGOING)){
				Record next = ref.getEndRecord();
				if(isValid(next)){
					searchOn(next, depth + 1);
				}
			}
		}
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
