package pishen.dblp;

import java.util.HashMap;

public class XMLRecord {
	private String recordKey;
	private HashMap<Key,String> recordMap = new HashMap<Key,String>();
	
	public XMLRecord(String recordKey){
		this.recordKey = recordKey;
	}
	
	public String getRecordKey(){
		return recordKey;
	}
	
	public void setProperty(Key key, String value){
		recordMap.put(key, value);
	}
	
	public String getProperty(Key key){
		return recordMap.get(key);
	}
	
	public boolean isValid(){
		if(recordKey != null && 
				recordMap.get(Key.TITLE) != null && 
				recordMap.get(Key.YEAR) != null && 
				recordMap.get(Key.EE) != null &&
				EEHandler.containsRuleForEE(recordMap.get(Key.EE))){
			return true;
		}else{
			return false;
		}
	}
}
