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
	int eeCount = 0;
	boolean stop = false;
	
	public void startParsing(){
		try {
			streamReader = inputFactory.createXMLStreamReader(new FileReader("dblp.xml"));
			parseForRecords();
			//\\//\\
			eeHandler.printResult();
			//\\//\\
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
	private void parseForRecords(){
		try {
			while(streamReader.hasNext() && stop == false){
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT && 
						isTargetPaperType(streamReader.getLocalName())){
					//match a target type record
					parseSingleRecord();
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
	private void parseSingleRecord(){
		try {
			System.out.println("parsing record " + (++eeCount));
			String recordKey = streamReader.getAttributeValue(null, "key");
			recordKey = recordKey.replaceAll("/", "-");
			while(streamReader.hasNext()){
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT &&
						streamReader.getLocalName().equals("ee")){
					//ee found
					streamReader.next();
					if(streamReader.getEventType() == XMLStreamReader.CHARACTERS){
						//handle a single record with ee value
						//TODO transfer the slash in KEY to dash
						//TODO move out as a function
						if(eeHandler.downloadRecord(recordKey, streamReader.getText())){
							stop = true;
						}
						//\\//\\//
						/*
						eeHandler.addEE(streamReader.getText());
						System.out.println("# of ee read=" + (++eeCount));
						*/
					}else{
						System.out.println("content of ee is not CHARACTERS!");
					}
				}else if(streamReader.getEventType() == XMLStreamReader.END_ELEMENT &&
						isTargetPaperType(streamReader.getLocalName())){
					//exit this record
					break;
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
