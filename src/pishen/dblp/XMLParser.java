package pishen.dblp;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import pishen.exception.DownloadFailException;

public class XMLParser {
	private EEHandler eeHandler = new EEHandler();
	private XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	private XMLStreamReader streamReader;
	private int recordCount = 0;
	private Record record;
	
	//TODO extract some higher level code to Controller, left only the parsing function here
	public void startParsing(){
		try {
			streamReader = inputFactory.createXMLStreamReader(new FileReader("dblp.xml"));
			parseAllRecords();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
	private void parseAllRecords() throws XMLStreamException{
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT && 
					isTargetPaperType(streamReader.getLocalName())){
				//match the record of <article> or <inproceedings>
				parseSingleRecord();
				//try to download the record and add it to DB
				if(record.getEEStr() != null){
					handleRecord();
				}
			}
		}
	}
	
	//handling records of <article> or <inproceedings>
	private void parseSingleRecord() throws XMLStreamException{
		record = new Record();
		record.setSlashKey(streamReader.getAttributeValue(null, "key"));
		
		System.out.println("# of record=" + (++recordCount) + " key=" + record.getSlashKey());
		
		//grabing information from the xml
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT &&
					streamReader.getLocalName().equals("ee")){
				//ee found
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.CHARACTERS){
					record.setEEStr(streamReader.getText());
				}else{
					System.out.println("content of ee is not CHARACTERS!");
				}
			}else if(streamReader.getEventType() == XMLStreamReader.END_ELEMENT &&
					isTargetPaperType(streamReader.getLocalName())){
				//finish parsing record
				break;
			}
		}
	}
	
	private void handleRecord(){
		try {
			eeHandler.downloadRecord(record);
			//TODO add the record to graphDB
		} catch (DownloadFailException e) {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isTargetPaperType(String localName){
		return localName.equals("article") || localName.equals("inproceedings");
	}
}
