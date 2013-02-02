package pishen.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import pishen.core.Key;

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
				isTargetDomain()){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isTargetDomain(){
		String domainName = null;
		
		try {
			domainName = new URL(recordMap.get(Key.EE)).getHost();
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
