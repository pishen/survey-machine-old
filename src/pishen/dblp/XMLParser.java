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
	private DBHandler dbHandler;
	
	public void setupReader(String inputFilename, DBHandler dbHandler) throws FileNotFoundException, XMLStreamException{
		streamReader = inputFactory.createXMLStreamReader(new FileReader(inputFilename));
		this.dbHandler = dbHandler;
	}
	
	/** Find the next record with record type "article" or "inproceedings",
	 * which also contains an EE url 
	 * 
	 * @return true if the next record is found
	 * @throws XMLStreamException
	 */
	public boolean hasNextRecord() throws XMLStreamException{
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT && 
					isTargetPaperType(streamReader.getLocalName())){
				return true;
			}
		}
		return false;
	}
	
	public Record getNextRecord() throws XMLStreamException{
		String recordKey = streamReader.getAttributeValue(null, "key");
		
		System.out.println("# " + (++recordCount) + " key=" + recordKey);
		
		Record record = dbHandler.getRecordWithKey(recordKey);
		record.setProperty(Key.FILENAME, recordKey.replaceAll("/", "-"));
		
		//grabing information from the xml
		while(streamReader.hasNext()){
			streamReader.next();
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT &&
					streamReader.getLocalName().equals("ee")){
				//ee found
				streamReader.next();
				if(streamReader.getEventType() == XMLStreamReader.CHARACTERS && streamReader.getText() != null){
					if(streamReader.getText().startsWith("db")){
						record.setProperty(Key.EE, "http://www.sigmod.org/dblp/" + streamReader.getText());
					}else{
						record.setProperty(Key.EE, streamReader.getText());
					}
				}else{
					System.out.println("content of ee is wrong!");
				}
			}else if(streamReader.getEventType() == XMLStreamReader.END_ELEMENT &&
					isTargetPaperType(streamReader.getLocalName())){
				//finish parsing record
				break;
			}
		}
		
		return record;
	}
	
	private boolean isTargetPaperType(String localName){
		return localName.equals("article") || localName.equals("inproceedings");
	}
	
}
