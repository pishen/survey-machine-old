package pishen.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MAPComputer {
	private HashMap<String, HashSet<String>> ansSetMap = new HashMap<String, HashSet<String>>();
	private ArrayList<Double> apList = new ArrayList<Double>();
	private int threshold;
	
	public MAPComputer(int threshold){
		this.threshold = threshold;
	}
	
	public double computeMap(File answerList, File rankList) throws IOException{
		fillAnsSetMap(answerList);
		parseRankList(rankList);
		
		double apSum = 0.0;
		for(double ap: apList){
			apSum += ap;
		}
		return apSum / (double)ansSetMap.size();
	}
	
	private void parseRankList(File rankList) throws IOException{
		ArrayList<String> singleRankList = null;
		String topic = null;
		BufferedReader in = new BufferedReader(new FileReader(rankList));
		String line = null;
		try {
			while((line = in.readLine()) != null){
				String[] elements = line.split(" ");
				if(elements.length != 2){
					//wrong ranked list format
					//throw new FormatException();
				}
				if(!elements[0].equals(topic)){
					computeAP(topic, singleRankList);
					topic = elements[0];
					singleRankList = new ArrayList<String>();
				}
				singleRankList.add(elements[1]);
			}
			computeAP(topic, singleRankList);
		} finally {
			in.close();
		}
	}
	
	private void computeAP(String topic, ArrayList<String> singleRankList){
		if(singleRankList == null || ansSetMap.get(topic) == null){
			return;
		}
		
		int matchCount = 0;
		double precisionSum = 0.0;
		for(int i = 0; i < singleRankList.size() && i < threshold; i++){
			String doc = singleRankList.get(i);
			if(ansSetMap.get(topic).contains(doc)){
				++matchCount;
				precisionSum += matchCount / (double)(i + 1);
			}
		}
		apList.add(precisionSum / (double)(ansSetMap.get(topic).size()));
	}
	
	private void fillAnsSetMap(File answerList) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(answerList));
		String line = null;
		while((line = in.readLine()) != null){
			String[] elements = line.split("\\s");
			if(!ansSetMap.containsKey(elements[0])){
				ansSetMap.put(elements[0], new HashSet<String>());
			}
			ansSetMap.get(elements[0]).add(elements[1]);
		}
		in.close();
	}
}
