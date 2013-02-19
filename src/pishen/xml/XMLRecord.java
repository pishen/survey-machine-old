package pishen.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pishen.core.RecordKey;

public class XMLRecord {
	private static final Logger log = Logger.getLogger(XMLRecord.class);
	private String recordName;
	private HashMap<RecordKey,String> recordMap = new HashMap<RecordKey,String>();
	
	public XMLRecord(String recordName){
		this.recordName = recordName;
	}
	
	public String getRecordName(){
		return recordName;
	}
	
	public void setProperty(RecordKey key, String value){
		recordMap.put(key, value);
	}
	
	public String getProperty(RecordKey key){
		return recordMap.get(key);
	}
	
	public boolean isValid(){
		if(recordName != null && 
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
			log.error("MalformedURLException on parsing EE");
			e.printStackTrace();
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
