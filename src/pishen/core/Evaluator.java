package pishen.core;

import java.util.ArrayList;
import java.util.List;

import pishen.db.Cite;
import pishen.db.Record;

public class Evaluator {
	private Record surveyRecord;
	private Record testRecord;
	private ArrayList<Record> ansRecords = new ArrayList<Record>();
	private int tp, tn, fp, fn;
	private double accuracy, recall, precision, f1;
	
	public Evaluator(TestCase testCase, List<Record> candidateList, List<Record> rankList){
		this.surveyRecord = testCase.getSurveyRecord();
		this.testRecord = testCase.getTestRecord();
		
		for(Cite cite: surveyRecord.getOutgoingCites()){
			Record candidateAns = cite.getEndRecord();
			if(!candidateAns.equals(testRecord) && candidateList.contains(candidateAns)){
				ansRecords.add(candidateAns);
			}
		}
		
		for(Record guess: rankList){
			if(ansRecords.contains(guess)){
				tp++;
			}else{
				fp++;
			}
		}
		
		fn = ansRecords.size() - tp;
		tn = candidateList.size() - ansRecords.size() - fp;
		
		accuracy = (tp + tn) / (double)(tp + fp + fn + tn);
		precision = tp / (double)(tp + fp);
		recall = tp / (double)(tp + fn);
		f1 = 2 * (precision * recall / (precision + recall));
	}
	
	public int getAnsSize(){
		return ansRecords.size();
	}
	
	public double getAccuracy(){
		return accuracy;
	}
	
	public double getPrecision(){
		return precision;
	}
	
	public double getRecall(){
		return recall;
	}
	
	public double getF1(){
		return f1;
	}
}
