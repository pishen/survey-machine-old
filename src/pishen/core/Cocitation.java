package pishen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import pishen.db.Cite;
import pishen.db.Record;

public class Cocitation {
	//private static final Logger log = Logger.getLogger(Cocitation.class);
	private ArrayList<Record> rankList = new ArrayList<Record>();
	private HashMap<Record, Integer> countMap = new HashMap<Record, Integer>();
	
	public Cocitation(List<Record> seedRecords, Record hidedRecord){
		for(Record seed: seedRecords){
			for(Cite stepOneCite: seed.getIncomingCites()){
				Record citingRecord = stepOneCite.getStartRecord();
				if(citingRecord.getCitationType() != Record.CitationType.NUMBER
						|| citingRecord.equals(hidedRecord)
						|| citingRecord.getYear() > hidedRecord.getYear()){
					continue;
				}
				for(Cite stepTwoCite: citingRecord.getOutgoingCites()){
					Record targetRecord = stepTwoCite.getEndRecord();
					if(targetRecord.getCitationType() != Record.CitationType.NUMBER
							|| seedRecords.contains(targetRecord)
							|| targetRecord.equals(hidedRecord)){
						continue;
					}
					if(countMap.containsKey(targetRecord)){
						countMap.put(targetRecord, countMap.get(targetRecord) + 1);
					}else{
						countMap.put(targetRecord, 1);
						rankList.add(targetRecord);
					}
				}
			}
		}
		Collections.sort(rankList, new rankRecordComparator());
	}
	
	private class rankRecordComparator implements Comparator<Record>{
		@Override
		public int compare(Record record0, Record record1) {
			//descending
			return countMap.get(record1) - countMap.get(record0);
		}
	}
	
	public List<Record> getRankList(int topK){
		List<Record> subRankList = new ArrayList<Record>();
		for(int i = 0; i < topK; i++){
			subRankList.add(rankList.get(i));
		}
		return subRankList;
	}
	
}
