package pishen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pishen.db.Cite;
import pishen.db.Record;

public class Cocitation {
	//private static final Logger log = Logger.getLogger(Cocitation.class);
	private ArrayList<RecordShell> candidateList = new ArrayList<RecordShell>();
	
	public Cocitation(TestCase testCase){
		Record testRecord = testCase.getTestRecord();
		Record surveyRecord = testCase.getSurveyRecord();
		int thresholdYear = testCase.getThresholdYear();
		
		//get records citing test record
		ArrayList<Record> citingRecords = new ArrayList<Record>();
		for(Cite cite: testRecord.getIncomingCites()){
			Record citingRecord = cite.getStartRecord();
			if(!citingRecord.equals(surveyRecord) && citingRecord.getYear() <= thresholdYear){
				citingRecords.add(citingRecord);
			}
		}
		
		//get all candidate records for ranking
		for(Record citingRecord: citingRecords){
			for(Cite cite: citingRecord.getOutgoingCites()){
				Record citedRecord = cite.getEndRecord();
				if(!citedRecord.equals(testRecord) && !citedRecord.equals(surveyRecord)){
					RecordShell recordShell = new RecordShell(citedRecord);
					int index = 0;
					if((index = candidateList.indexOf(recordShell)) >= 0){
						recordShell = candidateList.get(index);
					}else{
						candidateList.add(recordShell);
					}
					recordShell.addTimes();
				}
			}
		}
		//sort it
		Collections.sort(candidateList);
	}
	
	public ArrayList<Record> rank(int maxReturnSize){
		//make the ranklist
		ArrayList<Record> rankList = new ArrayList<Record>();
		for(int i = 0; i < maxReturnSize; i++){
			rankList.add(candidateList.get(i).getRecord());
		}
		
		return rankList;
	}
	
	public List<Record> getCandidateList(){
		List<Record> candidateRecordList = new ArrayList<Record>();
		for(RecordShell shell: candidateList){
			candidateRecordList.add(shell.getRecord());
		}
		return candidateRecordList;
	}
	
	private class RecordShell implements Comparable<RecordShell> {
		private int times;
		private Record record;
		
		public RecordShell(Record record){
			this.record = record;
		}
		
		public void addTimes(){
			times++;
		}
		
		public int getTimes(){
			return times;
		}
		
		public Record getRecord(){
			return record;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj == null || !(obj instanceof RecordShell)){
				return false;
			}
			RecordShell targetRecordShell = (RecordShell)obj;
			return this.record.equals(targetRecordShell.getRecord());
		}

		@Override
		public int compareTo(RecordShell targetRecordShell) {
			//descending order
			return targetRecordShell.getTimes() - this.times;
		}
		
	}
}
