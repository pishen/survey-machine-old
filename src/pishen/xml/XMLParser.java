package pishen.xml;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;

import pishen.core.Key;

public class XMLParser {
	private static final Logger log = Logger.getLogger(XMLParser.class);
	private XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	private XMLStreamReader streamReader;
	//private int recordCount = 0;
	private XMLRecord currentRecord = new XMLRecord("dummy");
	
	public XMLParser(String xmlFilename) throws FileNotFoundException, XMLStreamException{
		log.info("setting up XML reader...");
		streamReader = inputFactory.createXMLStreamReader(new FileReader(xmlFilename));
	}
	
	public XMLRecord getNextXMLRecord(){
		return currentRecord;
	}
	
	public boolean hasNextXMLRecord() throws XMLStreamException{
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT && 
					isTargetPaperType(streamReader.getLocalName())){
				parseXMLRecord();
				if(currentRecord.isValid()){
					return true;
				}
			}
		}
		return false;
	}
	
	private void parseXMLRecord() throws XMLStreamException{
		String recordKey = streamReader.getAttributeValue(null, "key");
		
		//log.info("# " + (++recordCount) + " key=" + recordKey);
		
		currentRecord = new XMLRecord(recordKey);
		currentRecord.setProperty(Key.FILENAME, recordKey.replaceAll("/", "-"));
		
		//grabing information from the xml
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT &&
					streamReader.getLocalName().equals("ee")){
				//ee found
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.CHARACTERS && streamReader.getText() != null){
					if(streamReader.getText().startsWith("db")){
						currentRecord.setProperty(Key.EE, "http://www.sigmod.org/dblp/" + streamReader.getText());
					}else{
						currentRecord.setProperty(Key.EE, streamReader.getText());
					}
				}else{
					log.error("content of ee is wrong");
				}
			}else if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT &&
					streamReader.getLocalName().equals("year")){
				//year found
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.CHARACTERS && streamReader.getText() != null){
					currentRecord.setProperty(Key.YEAR, streamReader.getText());
				}else{
					log.error("content of year is wrong");
				}
			}else if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT &&
					streamReader.getLocalName().equals("title")){
				//title found
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.CHARACTERS && streamReader.getText() != null){
					currentRecord.setProperty(Key.TITLE, streamReader.getText());
				}else{
					log.error("content of title is wrong");
				}
			}else if(streamReader.getEventType() == XMLStreamReader.END_ELEMENT &&
					isTargetPaperType(streamReader.getLocalName())){
				//finish parsing record
				break;
			}
		}
	}
	
	private boolean isTargetPaperType(String localName){
		return localName.equals("article") || localName.equals("inproceedings");
	}
	
}