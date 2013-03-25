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
	private ArrayList<String> incomingRecordNameList = new ArrayList<String>(); //names of Records that cite this record 
	
	public RecordConnectorACM(Record record){
		this.record = record;
	}
	
	public void connect(){
		try {
			validateRecord();
		} catch (Exception e) {
			return;
		}

		for(Cite cite: record.getOutgoingCites()){
			citeList.add(cite);
		}
		for(Cite incomingCite: record.getIncomingCites()){
			incomingRecordNameList.add(incomingCite.getStartRecord().getName());
		}
		
		for(HasRef hasRef: record.getHasRefs()){
			try {
				checkCiteExistence(hasRef);
				URL acmURL = getAcmUrlFromReference(hasRef);
				Record targetRecord = findTargetRecord(acmURL);
				detectLoop(targetRecord);
				Cite cite = record.createCiteTo(targetRecord, hasRef.getCitation());
				citeList.add(cite);
			} catch (Exception e) {
				continue;
			}
		}
	}
	
	private void validateRecord() throws Exception{
		if(record.getTextFile().exists() == false){
			log.info("textfile not exist");
			throw new Exception("textfile not exist");
		}
		if(record.getRefCount() == 0){
			log.info("no reference");
			throw new Exception("no reference");
		}
		if(record.getCitationType() != Record.CitationType.NUMBER){
			log.info("not supported citation type");
			throw new Exception("not supported citation type");
		}
	}
	
	private void checkCiteExistence(HasRef hasRef) throws Exception{
		for(Cite cite: citeList){
			if(hasRef.getCitation().equals(cite.getStringCitation())){
				throw new Exception("Cite already exists");
			}
		}
	}

	private URL getAcmUrlFromReference(HasRef hasRef) throws Exception{
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
			throw new Exception("no ACM URL found in HasRef");
		}else if(acmCount > 1){
			log.error("more than one matched URLs for ref with citation: " + hasRef.getCitation());
			throw new Exception("more than one ACM URL found in HasRef");
		}
		
		return acmURL;
	}
	
	private Record findTargetRecord(URL acmURL) throws Exception{
		RecordHits recordHits = Record.getRecordsWithEE(acmURL.toString());
		if(recordHits.size() == 0){
			throw new Exception("target Record not found");
		}else if(recordHits.size() > 1){
			log.error("more than one target Record with EE: " + acmURL.toString());
			recordHits.close();
			throw new Exception("more than one target Record found");
		}else{
			return recordHits.next();
		}
	}
	
	private void detectLoop(Record targetRecord) throws Exception{
		if(incomingRecordNameList.contains(targetRecord.getName())){
			throw new Exception("targetRecord already cite this record");
		}
	}
	
}
