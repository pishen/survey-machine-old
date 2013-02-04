package pishen.core;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.db.DBHandler;
import pishen.db.DBRecord;
import pishen.db.DBRecordIterator;
import pishen.exception.LinkingFailException;
import pishen.xml.XMLParser;
import pishen.xml.XMLRecord;

public class Controller {
	private static final Logger log = Logger.getLogger(Controller.class);
	private final String XML_FILENAME = "dblp.xml";
	
	public void startGraphDB(){
		DBHandler.startGraphDB();
	}
	
	public void copyXMLValuesToDB() throws Exception{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			//copy the key-value pairs from XMLRecord to database
			DBRecord dbRecord = DBHandler.getRecordWithKey(xmlRecord.getRecordKey());
			for(Key key: Key.values()){
				dbRecord.setProperty(key, xmlRecord.getProperty(key));
			}
		}
	}
	
	public void fetchResourcesForAllRecords(){
		DBRecordIterator iter = DBHandler.iteratorForRecord();
		while(iter.hasNext()){
			EEHandler.fetchResources(iter.next());
		}
	}
	
	public void linkRecords() throws FileNotFoundException, XMLStreamException{
		//TODO change the way of iterating to DB-based
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
		RecordLinker.writeTypeCounts();
	}
	
	//TODO feature require: updating property value by XMLParser and delete the record that's not exist anymore 
	
}
