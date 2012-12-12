package pishen.dblp;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLParser {
	private EEHandler eeHandler = new EEHandler();
	private XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	private XMLStreamReader streamReader;
	private int recordCount = 0;
	private boolean stop = false;
	
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
	
	private void parseAllRecords(){
		try {
			while(streamReader.hasNext() && stop == false){
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT && 
						isTargetPaperType(streamReader.getLocalName())){
					//match the record of <article> or <inproceedings>
					parseSingleRecord();
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
	private void parseSingleRecord(){
		try {
			System.out.println("parsing record " + (++recordCount));
			
			String recordKey = streamReader.getAttributeValue(null, "key").replaceAll("/", "-");
			String recordEE = null;
			boolean recordDownloaded = false;
			
			//grabing information from the xml
			while(streamReader.hasNext()){
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT &&
						streamReader.getLocalName().equals("ee")){
					//ee found
					streamReader.next();
					if(streamReader.getEventType() == XMLStreamReader.CHARACTERS){
						recordEE = streamReader.getText();
						/*
						eeHandler.addEE(streamReader.getText());
						System.out.println("# of ee read=" + (++eeCount));
						*/
					}else{
						System.out.println("content of ee is not CHARACTERS!");
					}
				}else if(streamReader.getEventType() == XMLStreamReader.END_ELEMENT &&
						isTargetPaperType(streamReader.getLocalName())){
					//finish parsing record
					break;
				}
			}
			
			//try to download the PDF in text version
			if(recordEE != null){
				recordDownloaded = eeHandler.downloadRecord(recordKey, recordEE);
				if(recordDownloaded){
					//TODO 
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isTargetPaperType(String localName){
		return localName.equals("article") || localName.equals("inproceedings");
	}
}
