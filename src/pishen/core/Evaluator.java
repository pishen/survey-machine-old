package pishen.core;

import java.util.ArrayList;
import java.util.List;

import pishen.db.Cite;
import pishen.db.Record;

public class Evaluator {
	private Record surveyRecord;
	private Record testRecord;
	private ArrayList<Record> ansRecords = new ArrayList<Record>();
	
	public Evaluator(TestCase testCase){
		this.surveyRecord = testCase.getSurveyRecord();
		this.testRecord = testCase.getTestRecord();
		
		for(Cite cite: surveyRecord.getOutgoingCites()){
			if(!cite.getEndRecord().equals(testRecord)){
				ansRecords.add(cite.getEndRecord());
			}
		}
	}
	
	public double computeF1(List<Record> rankList){
		int hit = 0;
		for(Record guess: rankList){
			if(ansRecords.contains(guess)){
				hit++;
			}
		}
		double precision = hit / (double)rankList.size();
		double recall = hit / (double)ansRecords.size();
		return 2 * (precision * recall / (precision + recall));
	}
}
