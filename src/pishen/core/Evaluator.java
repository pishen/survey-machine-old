package pishen.core;

import java.util.List;

import pishen.db.Record;

public class Evaluator {
	
	public double getMAP(List<ListBundle> bundleList){
		double sumAvgPrecision = 0.0;
		for(ListBundle bundle: bundleList){
			sumAvgPrecision += getAvgPrecision(bundle.ansList, bundle.rankList);
		}
		return sumAvgPrecision / (double)bundleList.size();
	}
	
	public class ListBundle{
		private List<Record> ansList;
		private List<Record> rankList;
		
		public ListBundle(List<Record> ansList, List<Record> rankList){
			this.ansList = ansList;
			this.rankList = rankList;
		}
	}
	
	public double getAvgPrecision(List<Record> ansList, List<Record> rankList){
		int hit = 0;
		double sumPrecision = 0.0;
		for(int i = 0; i < rankList.size(); i++){
			if(ansList.contains(rankList.get(i))){
				hit++;
				sumPrecision += hit / (double)(i + 1);
			}
		}
		return sumPrecision / (double)ansList.size();
	}
	
	public double getPrecision(List<Record> ansList, List<Record> rankList){
		return countHit(ansList, rankList) / (double)rankList.size();
	}
	
	public double getRecall(List<Record> ansList, List<Record> rankList){
		return countHit(ansList, rankList) / (double)ansList.size();
	}
	
	public double getF1(List<Record> ansList, List<Record> rankList){
		int hit = countHit(ansList, rankList);
		double precision = hit / (double)rankList.size();
		double recall = hit / (double)ansList.size();
		return (2 * precision * recall) / (precision + recall);
	}
	
	private int countHit(List<Record> ansList, List<Record> rankList){
		int hit = 0;
		for(int i = 0; i < rankList.size(); i++){
			if(ansList.contains(rankList.get(i))){
				hit++;
			}
		}
		return hit;
	}
	
}
