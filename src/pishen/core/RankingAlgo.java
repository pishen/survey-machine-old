package pishen.core;

import java.util.List;

import pishen.db.Record;

public interface RankingAlgo {
	public List<Record> rankOn(List<Record> seedRecords, Record sourceRecord);
	
	public static enum Type{
		Cocitation, Katz
	}
}
