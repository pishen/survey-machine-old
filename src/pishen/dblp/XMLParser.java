package pishen.dblp;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;

public class XMLParser {
	private static final Logger log = Logger.getLogger(XMLParser.class);
	private XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	private XMLStreamReader streamReader;
	private int recordCount = 0;
	
	public void setupReader(String inputFilename) throws FileNotFoundException, XMLStreamException{
		log.info("setting up XML reader...");
		streamReader = inputFactory.createXMLStreamReader(new FileReader(inputFilename));
	}
	
	/** Find the next record with record type "article" or "inproceedings",
	 * which also contains an EE url 
	 * 
	 * @return true if the next record is found
	 * @throws XMLStreamException
	 */
	public boolean hasNextXMLRecord() throws XMLStreamException{
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT && 
					isTargetPaperType(streamReader.getLocalName())){
				return true;
			}
		}
		return false;
	}
	
	public XMLRecord getNextXMLRecord() throws XMLStreamException{
		String recordKey = streamReader.getAttributeValue(null, "key");
		
		log.info("# " + (++recordCount) + " key=" + recordKey);
		
		XMLRecord xmlRecord = new XMLRecord(recordKey);
		xmlRecord.setProperty(Key.FILENAME, recordKey.replaceAll("/", "-"));
		
		//grabing information from the xml
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT &&
					streamReader.getLocalName().equals("ee")){
				//ee found
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.CHARACTERS && streamReader.getText() != null){
					if(streamReader.getText().startsWith("db")){
						xmlRecord.setProperty(Key.EE, "http://www.sigmod.org/dblp/" + streamReader.getText());
					}else{
						xmlRecord.setProperty(Key.EE, streamReader.getText());
					}
				}else{
					log.warn("content of ee is wrong");
				}
			}else if(streamReader.getEventType() == XMLStreamReader.END_ELEMENT &&
					isTargetPaperType(streamReader.getLocalName())){
				//finish parsing record
				break;
			}
		}
		
		return xmlRecord;
	}
	
	private boolean isTargetPaperType(String localName){
		return localName.equals("article") || localName.equals("inproceedings");
	}
	
}
