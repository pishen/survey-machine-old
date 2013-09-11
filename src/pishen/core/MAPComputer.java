package pishen.core;

import java.util.List;

import pishen.db.Record;


public class MAPComputer {
	private int threshold;
	
	public MAPComputer(int threshold){
		this.threshold = threshold;
	}
	
	/*public double computeMAPOn(List<TestCase> testCases, RankingAlgo.Type type){
		double apSum = 0.0;
		for(TestCase testCase: testCases){
			apSum += computeAPOn(testCase.getRankRecords(type), testCase.getAnsRecords());
		}
		return apSum / (double)testCases.size();
	}*/
	
	public double computeSumAPOn(List<TestCase> testCases, RankingAlgo.Type type){
		double apSum = 0.0;
		for(TestCase testCase: testCases){
			apSum += computeAPOn(testCase.getRankRecords(type), testCase.getAnsRecords());
		}
		return apSum;
	}
	
	private double computeAPOn(List<Record> rankRecords, List<Record> ansRecords){
		int matchCount = 0;
		double precisionSum = 0.0;
		for(int i = 0; i < rankRecords.size() && i < threshold; i++){
			if(ansRecords.contains(rankRecords.get(i))){
				matchCount++;
				precisionSum += matchCount / (double)(i + 1);
			}
		}
		return precisionSum / (double)ansRecords.size();
	}
}
