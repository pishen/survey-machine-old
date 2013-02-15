package pishen.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import pishen.core.RecordKey;

public class XMLRecord {
	private String recordKey;
	private HashMap<RecordKey,String> recordMap = new HashMap<RecordKey,String>();
	
	public XMLRecord(String recordKey){
		this.recordKey = recordKey;
	}
	
	public String getRecordKey(){
		return recordKey;
	}
	
	public void setProperty(RecordKey key, String value){
		recordMap.put(key, value);
	}
	
	public String getProperty(RecordKey key){
		return recordMap.get(key);
	}
	
	public boolean isValid(){
		if(recordKey != null && 
				recordMap.get(RecordKey.TITLE) != null && 
				recordMap.get(RecordKey.YEAR) != null && 
				recordMap.get(RecordKey.EE) != null &&
				isTargetDomain()){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isTargetDomain(){
		String domainName = null;
		
		try {
			domainName = new URL(recordMap.get(RecordKey.EE)).getHost();
		} catch (MalformedURLException e) {
			return false;
		}
		
		//TODO add more domains
		if(domainName.equals("doi.acm.org")){
			return true;
		}else{
			return false;
		}
	}
}
