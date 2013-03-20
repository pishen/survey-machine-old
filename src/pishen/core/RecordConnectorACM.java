package pishen.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import pishen.db.Cite;
import pishen.db.HasRef;
import pishen.db.Record;
import pishen.db.RecordHits;

public class RecordConnectorACM {
	private static final Logger log = Logger.getLogger(RefFetcherACM.class);
	private Record record;
	private ArrayList<Cite> citeList = new ArrayList<Cite>();
	
	public RecordConnectorACM(Record record){
		this.record = record;
	}
	
	public void connect(){
		if(isValidRecord() == false){return;}

		for(Cite cite: record.getCites()){
			citeList.add(cite);
		}
		
		for(HasRef hasRef: record.getHasRefs()){
			URL acmURL = getAcmUrlFromReference(hasRef);
			if(acmURL == null){
				continue;
			}
			if(isCiteExist(hasRef)){
				continue;
			}
			
			Record targetRecord = findTargetRecord(acmURL);
			if(targetRecord == null){
				continue;
			}
			
			//TODO create Relationship CITE
			
		}
	}
	
	private boolean isValidRecord(){
		if(record.getTextFile().exists() == false){
			log.info("[CONNECT] textfile not exist");
			return false;
		}
		if(record.getRefCount() == 0){
			log.info("[CONNECT] no reference");
			return false;
		}
		if(record.getCitationType() != Record.CitationType.NUMBER){
			log.info("[CONNECT] not supported citation type");
			return false;
		}
		return true;
	}
	
	private URL getAcmUrlFromReference(HasRef hasRef){
		URL acmURL = null;
		
		int acmCount = 0;
		for(String linkStr: hasRef.getReference().getLinks()){
			try {
				URL linkURL = new URL(linkStr);
				if(linkURL.getHost().equals("doi.acm.org")){
					acmURL = linkURL;
					acmCount++;
				}
			} catch (MalformedURLException e) {
				continue;
			}
		}
		
		if(acmCount == 0){
			return null;
		}else if(acmCount > 1){
			log.error("[CONNECT] more than one matched URLs for ref with citation: " + hasRef.getIntCitation());
			return null;
		}
		
		return acmURL;
	}
	
	private boolean isCiteExist(HasRef hasRef){
		for(Cite cite: citeList){
			if(Integer.toString(hasRef.getIntCitation()).equals(cite.getStringCitation())){
				return true;
			}
		}
		return false;
	}
	
	private Record findTargetRecord(URL acmURL){
		RecordHits recordHits = Record.getRecordsWithEE(acmURL.toString());
		if(recordHits.size() == 0){
			return null;
		}else if(recordHits.size() > 1){
			log.error("[CONENCT] more than one record with EE: " + acmURL.toString());
			recordHits.close();
			return null;
		}else{
			return recordHits.next();
		}
	}
	
}
