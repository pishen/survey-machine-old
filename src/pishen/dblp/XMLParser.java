package pishen.dblp;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLParser {
	
	private XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	private XMLStreamReader streamReader;
	private int recordCount = 0;
	private Record currentRecord;
	
	public void setupReader() throws FileNotFoundException, XMLStreamException{
		streamReader = inputFactory.createXMLStreamReader(new FileReader("dblp.xml"));
	}
	
	/** Find the next record with record type "article" or "inproceedings",
	 * which also contains an EE url 
	 * 
	 * @return true if the next record is found
	 * @throws XMLStreamException
	 */
	public boolean parseForNextRecord() throws XMLStreamException{
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT && 
					isTargetPaperType(streamReader.getLocalName())){
				grabInfoFromRecord();
				if(currentRecord.getEEStr() != null){
					return true;
				}
			}
		}
		return false;
	}
	
	private void grabInfoFromRecord() throws XMLStreamException{
		currentRecord = new Record();
		currentRecord.setSlashKey(streamReader.getAttributeValue(null, "key"));
		
		System.out.println("# " + (++recordCount) + " key=" + currentRecord.getSlashKey());
		
		//grabing information from the xml
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT &&
					streamReader.getLocalName().equals("ee")){
				//ee found
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.CHARACTERS){
					currentRecord.setEEStr(streamReader.getText());
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
	
	private boolean isTargetPaperType(String localName){
		return localName.equals("article") || localName.equals("inproceedings");
	}
	
	public Record getCurrentRecord(){
		return currentRecord;
	}
}
