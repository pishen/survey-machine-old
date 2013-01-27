package pishen.dblp;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.exception.DownloadFailException;
import pishen.exception.LinkingFailException;

public class Controller {
	private static final Logger log = Logger.getLogger(Controller.class);
	private final String XML_FILENAME = "dblp.xml";
	
	public void startGraphDB(){
		DBHandler.startGraphDB();
	}
	
	public void downloadRecords() throws FileNotFoundException, XMLStreamException{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			//copy the key-value pairs from XMLRecord to database
			DBRecord dbRecord = DBHandler.getRecordWithKey(xmlRecord.getRecordKey());
			for(Key key: Key.values()){
				dbRecord.setProperty(key, xmlRecord.getProperty(key));
			}
			//try to download EE
			try {
				EEHandler.downloadRecord(dbRecord);
			} catch (DownloadFailException e) {
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			log.info("===SUCCESS===");
		}
	}
	
	public void linkRecords() throws FileNotFoundException, XMLStreamException{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			DBRecord dbRecord = DBHandler.getRecordWithKey(xmlRecord.getRecordKey());
			try {
				RecordLinker.linkRecord(dbRecord);
			} catch (LinkingFailException e) {
				continue;
			}
		}
		
	}
	
	//TODO feature require: updating property value by XMLParser and delete the record that's not exist anymore 
	
}
